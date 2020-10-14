package app.automs.sdk;

import app.automs.sdk.config.StromProperties;
import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.config.EnvContext;
import app.automs.sdk.domain.config.headless.ChromeDriverOptionsConfig;
import app.automs.sdk.domain.config.store.PageScreenCaptureConfig;
import app.automs.sdk.domain.http.AutomationInput;
import app.automs.sdk.domain.http.AutomationResponse;
import app.automs.sdk.domain.store.SessionFile;
import app.automs.sdk.domain.store.StoreContainer;
import app.automs.sdk.traits.AutomationFunction;
import app.automs.sdk.traits.Storable;
import app.automs.sdk.traits.Webdriver;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static app.automs.sdk.domain.AutomationState.*;
import static app.automs.sdk.helper.HtmlHelper.getStringBytesEncodedAs;
import static app.automs.sdk.helper.HtmlHelper.prepareHtmlFile;
import static app.automs.sdk.helper.ScreenshotHelper.takeFullPageScreenShotAsByte;
import static java.lang.String.join;
import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.*;
import static org.openqa.selenium.OutputType.BYTES;

// @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
// http://www.javabyexamples.com/scoped-beans-as-dependencies-in-spring
@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "unused"})
abstract public class StromAutomation implements AutomationFunction, Webdriver, Storable {

    protected WebDriver driver;
    protected JavascriptExecutor js;
    protected TakesScreenshot ts;
    private String webdriverEndpoint;
    @Autowired
    private StromStorage storage;
    @Autowired
    private EnvContext context;
    @Autowired
    private StromProperties properties;

    protected AutomationInput before(@NotNull AutomationRecipe recipe) {
        recipe.getAutomationInput().getInputParams()
                .put("requestId", recipe.getRequestId());
        return recipe.getAutomationInput();
    }

    // TODO make this method final, although Mockito tests will break
    public AutomationResponse<?> run(@NotNull AutomationRecipe recipe) {
        AutomationResponse<?> response;
        // configure the base objects storage path
        val objectPath = storage.parseStoragePath(recipe);
        try {
            // hard validation of the request resource
            validateRequestedResource(recipe);

            // run pre-process function
            val input = before(recipe);

            // execute recipe
            setup(recipe);
            response = execute(input);

            // automation sanity check
            val validationResponse = validate(response);
            if (!validationResponse.getIsValid()) {
                response.setStatus(VALIDATION_FAIL);
                response.setCustomResponse("failed validations: " + join(", ", validationResponse.getErrorMessages()));
                response.setSessionFiles(emptyList());
                // attempt to take screenshot on validation error
                attemptScreenshot(objectPath);
            }

            if (response.getStatus() == PROCESSED) {
                // automation formal storage
                store(new StoreContainer(recipe.getConfig(), response, driver.getCurrentUrl(),
                        driver.getPageSource(), objectPath));

                response.setStatus(SUCCESSFUL);
            }

        } catch (Exception e) {
            log.error("Error running automation", e);
            //noinspection rawtypes
            response = new AutomationResponse();
            response.setCustomResponse(
                    MessageFormat.format("Failed at stage [{0}], " +
                            "root cause is: ({1})", response.getStatus(), e.getMessage())
            );
            response.setStatus(ERROR);

            // attempt to take screenshot on error
            attemptScreenshot(objectPath);

        } finally {
            if (driver != null) {
                if (recipe.getConfig().getChromeDriverOptionsConfig().getQuitSession()) {
                    driver.quit();
                } else {
                    driver.close();
                }
            } else {
                //noinspection PlaceholderCountMatchesArgumentCount
                log.error("current session seen premature finalized", recipe);
            }
        }

        return response;
    }

    private void setup(@NotNull AutomationRecipe recipe) {
        // configure/get the browser driver
        driver = getDriver(recipe.getConfig().getChromeDriverOptionsConfig());

        // set up driver add-ons components
        js = (JavascriptExecutor) driver;
        ts = (TakesScreenshot) driver;

    }

    private AutomationResponse<?> execute(AutomationInput input) {
        // start the automation
        driver.get(entryPointUrl());
        log.info(String.format("driver ready, automation using entry point url: [%s]", entryPointUrl()));

        val response = process(input);
        response.setStatus(PROCESSED);

        log.info(String.format("automation process done, last visited url: [%s - %s]",
                driver.getCurrentUrl(), driver.getTitle()));
        // end of the automation the automation
        return response;
    }

    private void attemptScreenshot(String objectPath) {
        try {
            if (driver != null) {
                driver.manage().window().setSize(new Dimension(1366, 2700));
                val filepath = String.format("%s-failure.png", objectPath);
                storage.createFile(filepath, ts.getScreenshotAs(BYTES));
            }
        } catch (Exception ignored) {
        }
    }

    private void validateRequestedResource(@NotNull AutomationRecipe recipe) {
        val requestedResourceId = recipe.getAutomationResourceId();
        if (!Objects.equals(requestedResourceId, properties.getResourceId())) {
            throw new IllegalArgumentException(
                    String.format("recipe resource does not match with the requested [%s] given [%s]",
                            properties.getResourceId(), requestedResourceId));
        }
    }

    final public void store(final @NotNull StoreContainer container) {
        val config = container.getConfig();
        val pageConfig = config.getPageCopyConfig();
        val objectPath = container.getObjectPath();

        // html page
        if (pageConfig.getEnablePageCopy()) {
            val filepath = String.format("%s.html", objectPath);
            var pageSource = container.getGivenPageSource();

            if (pageConfig.getUsingAbsolutLinks()) {
                pageSource = prepareHtmlFile(pageSource, container.getExitPointUrl(), pageConfig);
            }

            storage.createFile(filepath, getStringBytesEncodedAs(pageSource, pageConfig.getUsingCharset()));
        }

        // json response
        if (config.getPageCopyConfig().getEnableStructuredResponseCopy()) {
            val filepath = String.format("%s.json", objectPath);
            storage.createFile(filepath, container.getResponse());
        }

        // screenshot
        val screenshotConfig = config.getPageCaptureConfig();
        if (screenshotConfig.getEnableScreenCapture()) {
            storeScreenshot(screenshotConfig, objectPath);
        }

        // store arbitrary given bytes
        val sessionFiles = container.getResponse().getSessionFiles();
        storeAutomationSessionFiles(objectPath, sessionFiles);
    }

    private void storeAutomationSessionFiles(String objectPath, List<SessionFile> sessionFiles) {
        if (!sessionFiles.isEmpty()) {
            var index = 0;
            // user files qty hard limit
            assert !(sessionFiles.size() > 10);
            for (val sessionFile : sessionFiles) {
                // hard size in bytes 5,25 MB per user file
                assert !(sessionFile.getContent().length > 5_500_000);
                val suffix = sessionFile.getLabel();
                val extension = sessionFile.getExtension().getValue();
                // bucket/order/request/1602199186-sample-automation-asset-udf1.pdf
                val filepath = format("{0}-{1}{2}{3}", objectPath, suffix, ++index, extension);
                storage.createFile(filepath, sessionFile.getContent());
            }
        }
    }

    @SneakyThrows
    private void storeScreenshot(final PageScreenCaptureConfig config, String objectPath) {
        assert config.getEnableScreenCapture() : "store call not allowed, since storage config = false";
        val filepath = String.format("%s.png", objectPath);
        switch (config.getCaptureType()) {
            case ELEMENT:
                By targetElement = null;
                switch (config.getElementType()) {
                    case XPATH:
                        targetElement = xpath(config.getTargetElement());
                        break;
                    case ID:
                        targetElement = id(config.getTargetElement());
                        break;
                    case NAME:
                        targetElement = name(config.getTargetElement());
                        break;
                    case CLASSNAME:
                        targetElement = className(config.getTargetElement());
                        break;
                    case SELECTOR:
                        targetElement = cssSelector(config.getTargetElement());
                        break;
                }
                val elementBytes = driver.findElement(targetElement).getScreenshotAs(BYTES);
                storage.createFile(filepath, elementBytes);
                break;
            case FULL_PAGE:
                storage.createFile(filepath, takeFullPageScreenShotAsByte(driver));
                break;
            case WINDOW:
                storage.createFile(filepath, ts.getScreenshotAs(BYTES));
                break;
        }
    }

    // override for change it
    public void setAutomationHardTimeoutLimit(@NotNull WebDriver driver, long seconds) {
        val driverTimeoutConf = driver.manage().timeouts();
        driverTimeoutConf.implicitlyWait(seconds, SECONDS);
        //driverTimeoutConf.pageLoadTimeout(seconds, SECONDS);
    }

    private WebDriver getDriver(ChromeDriverOptionsConfig config) {
        config = (config == null) ? new ChromeDriverOptionsConfig() : config;
        val driver = withRemoteWebdriver(resolveEndpoint(properties.getWebdriver()), prepareHeadlessBrowser(config));

        setAutomationHardTimeoutLimit(driver, config.getElementSearchTimeout());
        return driver;
    }

    private String resolveEndpoint(String uri) {
        try {
            webdriverEndpoint = context.isCloud() || uri.endsWith("9090") ? Unirest.get(uri).asString().getBody() : uri;
            assert !webdriverEndpoint.contains("Bad Gateway") : "LB Fail: Bad Gateway";
        } catch (Exception e) {
            webdriverEndpoint = properties.getWebdriverFb();
            log.error(String.format("using fb-endpoint [%s]", webdriverEndpoint), e);
        }
        log.info(String.format("using endpoint [%s]", webdriverEndpoint));
        return webdriverEndpoint;
    }

    public byte[] pickFile(String requestId, String filename) {
        return storage.getDownloadedFile(webdriverEndpoint, requestId, filename);
    }
}

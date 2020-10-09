package app.automs.sdk;

import app.automs.sdk.config.StromProperties;
import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.config.headless.ChromeDriverOptionsConfig;
import app.automs.sdk.domain.config.store.StoreScreenshotConfig;
import app.automs.sdk.domain.http.AutomationResponse;
import app.automs.sdk.domain.store.AdditionalFile;
import app.automs.sdk.domain.store.StoreContainer;
import app.automs.sdk.traits.Function;
import app.automs.sdk.traits.Storable;
import app.automs.sdk.traits.Webdriver;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static app.automs.sdk.domain.AutomationProcessingStatus.*;
import static app.automs.sdk.helper.HtmlHelper.getStringBytesEncodedAs;
import static app.automs.sdk.helper.HtmlHelper.prepareHtmlFile;
import static app.automs.sdk.helper.ScreenshotHelper.takeFullPageScreenShotAsByte;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.*;
import static org.openqa.selenium.OutputType.BYTES;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
abstract public class StromAutomation implements Webdriver, Storable, Function {

    @Autowired
    public StromProperties properties;
    protected WebDriver driver;
    protected JavascriptExecutor js;
    protected TakesScreenshot ts;
    @Autowired
    private StromStorage storage;

    protected abstract AutomationResponse<?> process(Map<String, String> args);

    protected abstract Boolean validate(@NotNull AutomationResponse<?> response);

    protected abstract String entryPointUrl();

    // TODO make this method final, although Mockito tests will break
    public AutomationResponse<?> run(AutomationRecipe recipe) {
        AutomationResponse<?> processResponse;
        // configure the base objects storage path
        val objectPath = storage.parseStoragePath(recipe);
        try {
            // configure/get the browser driver
            driver = getDriver(recipe.getConfig().getChromeDriverOptionsConfig());

            // hard validation of the request resource
            checkRequestedResource(recipe);

            // set up driver add-ons components
            js = (JavascriptExecutor) driver;
            ts = (TakesScreenshot) driver;

            // start the automation
            driver.get(entryPointUrl());
            log.info(String.format("driver ready, automation using entry point url: [%s]", entryPointUrl()));

            processResponse = process(recipe.getInputParams());

            val exitPointUrl = driver.getCurrentUrl();
            val pageSource = driver.getPageSource();
            log.info(String.format("automation process done, last visited url: [%s - %s]", exitPointUrl, driver.getTitle()));
            // end of the automation the automation

            // automation sanity check
            if (!validate(processResponse)) {
                processResponse.setProcessingStatus(VALIDATION_FAIL);
            }

            // automation formal storage
            store(new StoreContainer(recipe,
                    processResponse,
                    exitPointUrl,
                    pageSource,
                    objectPath));

            processResponse.setProcessingStatus(OK);

        } catch (Exception e) {
            log.error("Error processing recipe", e);
            //noinspection rawtypes
            processResponse = new AutomationResponse();
            processResponse.setProcessingStatus(INTERNAL_ERROR);
            processResponse.setMessageResponse(e.getMessage());

            // attempt to take screenshot on error
            attemptScreenshot(objectPath);

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return processResponse;
    }

    private void attemptScreenshot(String objectPath) {
        try {
            if (driver != null) {
                // manage().window().setSize(new Dimension(1920, 1057));
                val filepath = String.format("%s-failure.png", objectPath);
                storage.createFile(filepath, ts.getScreenshotAs(BYTES));
            }
        } catch (Exception ignored) {
        }
    }

    private void checkRequestedResource(@NotNull AutomationRecipe recipe) {
        val requestedResourceId = recipe.getAutomationResourceId();
        if (!Objects.equals(requestedResourceId, properties.getResourceId())) {
            throw new IllegalArgumentException(
                    String.format("recipe resource does not match with the requested [%s] given [%s]",
                            properties.getResourceId(), requestedResourceId));
        }
    }


    final public void store(final @NotNull StoreContainer container) {
        val config = container.getRecipe().getConfig();
        val pageConfig = config.getPageCopyConfig();
        val objectPath = container.getObjectPath();

        // html page
        if (pageConfig.getStorePage()) {
            val filepath = String.format("%s.html", objectPath);
            var pageSource = container.getGivenPageSource();

            if (pageConfig.getEnforceAbsolutLinks()) {
                pageSource = prepareHtmlFile(pageSource, container.getExitPointUrl(), pageConfig);
            }

            storage.createFile(filepath, getStringBytesEncodedAs(pageSource, pageConfig.getCharset()));
        }

        // json response
        if (config.getStructuredConfig().getStoreResponseAsJson()) {
            val filepath = String.format("%s.json", objectPath);
            storage.createFile(filepath, container.getResponse());
        }

        // screenshot
        val screenshotConfig = config.getScreenshotConfig();
        if (screenshotConfig.getStoreScreenshot()) {
            storeScreenshot(screenshotConfig, objectPath);
        }

        // store arbitrary given bytes
        val userFiles = container.getRecipe().getResponse().getAdditionalFile();
        storeAutomationDefinedFiles(objectPath, userFiles);
    }

    private void storeAutomationDefinedFiles(String objectPath, List<AdditionalFile> userFiles) {
        if (!userFiles.isEmpty()) {
            var index = 0;
            // user files qty hard limit
            assert !(userFiles.size() > 10);
            for (val userFile : userFiles) {
                // hard size in bytes 5,25 MB per user file
                assert !(userFile.getContent().length > 5_500_000);
                val suffix = userFile.getLabel();
                val extension = userFile.getExtension().getValue();
                // bucket/order/request/1602199186-sample-automation-asset-udf1.pdf
                val filepath = format("{0}-{1}{2}.{3}", objectPath, suffix, ++index, extension);
                storage.createFile(filepath, userFile.getContent());
            }
        }
//    range(0, userFiles.size())
//            .mapToObj(index -> Pair.of(index, userFiles.get(index)))
//            .filter(pair -> pair.getRight().getContent().length <= 5_500_000)
//            .forEachOrdered(s -> s);
    }

    @SneakyThrows
    private void storeScreenshot(final StoreScreenshotConfig config, String objectPath) {
        assert config.getStoreScreenshot() : "store call not allowed, since storage config = false";
        val filepath = String.format("%s.png", objectPath);
        switch (config.getTarget()) {
            case ELEMENT:
                By targetElement = null;
                switch (config.getElementType()) {
                    case XPATH:
                        targetElement = xpath(config.getWithElement());
                        break;
                    case ID:
                        targetElement = id(config.getWithElement());
                        break;
                    case NAME:
                        targetElement = name(config.getWithElement());
                        break;
                    case CLASSNAME:
                        targetElement = className(config.getWithElement());
                        break;
                    case SELECTOR:
                        targetElement = cssSelector(config.getWithElement());
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
        config = config == null ? new ChromeDriverOptionsConfig() : config;
        val driver = withRemoteWebdriver(properties.getWebdriver(), prepareHeadlessBrowser(config));
        setAutomationHardTimeoutLimit(driver, config.getElementSearchTimeout());
        return driver;
    }
}

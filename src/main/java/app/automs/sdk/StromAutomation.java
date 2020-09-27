package app.automs.sdk;

import app.automs.sdk.config.StromProperties;
import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationResponse;
import app.automs.sdk.traits.HtmlParser;
import app.automs.sdk.traits.StorageHandler;
import app.automs.sdk.traits.Webdriver;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static app.automs.sdk.domain.AutomationProcessingStatus.*;
import static app.automs.sdk.helper.CharsetHelper.getStringBytesEncodedAs;
import static app.automs.sdk.helper.ScreenshootHelper.takeFullPageScreenShotAsByte;
import static java.text.MessageFormat.format;
import static org.openqa.selenium.By.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
abstract public class StromAutomation implements Webdriver, HtmlParser, StorageHandler {

    @Autowired
    public StromProperties properties;
    protected WebDriver driver;
    @Autowired
    private Storage storage;
    @Autowired
    private Gson gson;

    protected abstract AutomationResponse<?> process(Map<String, String> args);

    protected abstract Boolean validate(@NotNull AutomationResponse<?> response);

    protected abstract String entryPointUrl();

    public AutomationResponse<?> run(AutomationRecipe recipe) {
        AutomationResponse<?> recipeResponse;
        try {
            log.info(
                    String.format("starting automation process, requestId%s automationId: %s",
                            recipe.getRequestId(), recipe.getAutomationResourceId())
            );
            driver = getDriver();
            checkRequestedResource(recipe);
            driver.get(entryPointUrl());
            log.info(String.format("driver ready, automation using entry point url: [%s]", entryPointUrl()));

            recipeResponse = process(recipe.getInputParams());

            val exitPointUrl = driver.getCurrentUrl();
            log.info(String.format("automation process done, last visited url: [%s - %s]", exitPointUrl, driver.getTitle()));

            if (!validate(recipeResponse)) {
                recipeResponse.setProcessingStatus(VALIDATION_FAIL);
            }

            store(recipe, recipeResponse, exitPointUrl, driver.getPageSource());
            recipeResponse.setProcessingStatus(OK);

        } catch (Exception e) {
            //noinspection rawtypes
            recipeResponse = new AutomationResponse();
            recipeResponse.setProcessingStatus(INTERNAL_ERROR);
            recipeResponse.setMessageResponse(e.getMessage());
            log.error("Error processing recipe", e);

        } finally {
            driver.quit();
        }

        return recipeResponse;
    }

    private void checkRequestedResource(AutomationRecipe recipe) {
        val requestedResourceId = recipe.getAutomationResourceId();
        if (!Objects.equals(requestedResourceId, properties.getResourceId())) {
            throw new IllegalArgumentException(
                    String.format("recipe resource does not match with the requested [%s] given [%s]",
                            properties.getResourceId(), requestedResourceId));
        }
    }

    @SneakyThrows
    protected void store(final @NotNull AutomationRecipe recipe,
                         final @NotNull AutomationResponse<?> response,
                         final @NotNull String exitPointUrl,
                         final @NotNull String givenPageSource) {
        val config = recipe.getConfig();
        val objectName = String.format("%s-asset", recipe.getAutomationResourceId().split("/")[1]);
        val objectPath = format("{0}/{1}/{2}", recipe.getOrderId(), recipe.getRequestId(), objectName);

        val pageConfig = config.getPageCopyConfig();
        if (pageConfig.getStorePage()) {
            val filepath = String.format("%s.html", objectPath);

            var pageSource = givenPageSource;

            if (pageConfig.getEnforceAbsolutLinks()) {
                pageSource = appendAbsolutPath(givenPageSource, exitPointUrl);
            }

            createFile(filepath, getStringBytesEncodedAs(pageSource, pageConfig.getCharset()));
        }

        val shootConfig = config.getScreenshotConfig();
        if (shootConfig.getStoreScreenshot()) {
            val filepath = String.format("%s.png", objectPath);
            switch (shootConfig.getTarget()) {
                case ELEMENT:
                    By tartgetElement = null;
                    switch (shootConfig.getElementType()) {
                        case XPATH:
                            tartgetElement = xpath(shootConfig.getWithElement());
                            break;
                        case ID:
                            tartgetElement = id(shootConfig.getWithElement());
                            break;
                        case NAME:
                            tartgetElement = name(shootConfig.getWithElement());
                            break;
                        case CLASSNAME:
                            tartgetElement = className(shootConfig.getWithElement());
                            break;
                        case SELECTOR:
                            tartgetElement = cssSelector(shootConfig.getWithElement());
                            break;
                    }
                    val elementBytes = driver.findElement(tartgetElement).getScreenshotAs(OutputType.BYTES);
                    createFile(filepath, elementBytes);
                    break;
                case FULLPAGE:
                    createFile(filepath, takeFullPageScreenShotAsByte(driver));
                    break;
                case WINDOW:
                    val ts = (TakesScreenshot) driver;
                    createFile(filepath, ts.getScreenshotAs(OutputType.BYTES));
                    break;
            }
        }

        if (config.getJsonResponseConfig().getStoreJsonResponse()) {
            val filepath = String.format("%s.json", objectPath);
            createFile(filepath, getEntityAsJson(response).getBytes());
        }
    }

    @SneakyThrows
    protected void createFile(String filepath, byte[] bytes) {
        // write gcs
        val blobId = BlobId.of(properties.getBaseBucket(), filepath);
        val blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, bytes);

        // write local
        val filename = filepath.split("/")[2];
        val path = Paths.get(filename);
        Files.write(path, bytes);
    }

    protected void withDriverConfig(@NotNull WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    private String getEntityAsJson(final AutomationResponse<?> response) {
        return gson.toJson(response.getResponseEntity());
    }

    private WebDriver getDriver() {
        val driver = withRemoteWebdriver(properties.getWebdriver(), prepareHeadlessBrowser());
        withDriverConfig(driver);
        return driver;
    }
}

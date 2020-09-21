package app.automs.internal;

import app.automs.internal.domain.AutomationConfig;
import app.automs.internal.domain.AutomationRecipe;
import app.automs.internal.domain.AutomationResponse;
import app.automs.internal.traits.StromPdfHandler;
import app.automs.internal.traits.StromWebdriver;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static app.automs.internal.domain.AutomationProcessingStatus.*;
import static java.text.MessageFormat.format;

abstract public class StromAutomation implements StromWebdriver, StromPdfHandler {

    @Value("${automs.automation.resourceId}")
    public String resourceId;
    protected WebDriver driver;
    @Value("${automs.webdriver.url}")
    private String webdriverUri;
    @Value("${automs.baseBucket}")
    private String baseBucket;
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
            driver = getDriver();
            driver.get(entryPointUrl());

            recipeResponse = process(recipe.getInputParams());

            if (!validate(recipeResponse)) {
                recipeResponse.setProcessingStatus(VALIDATION_FAIL);
            }

            store(recipe, recipeResponse);
            recipeResponse.setProcessingStatus(OK);

        } catch (Exception e) {
            //noinspection rawtypes
            recipeResponse = new AutomationResponse();
            recipeResponse.setProcessingStatus(INTERNAL_ERROR);
            recipeResponse.setMessageResponse(e.getMessage());
            logger.error("Error processing recipe", e);

        } finally {
            driver.quit();
        }

        return recipeResponse;
    }

    protected void store(final @NotNull AutomationRecipe recipe,
                         final @NotNull AutomationResponse<?> response) throws IOException {
        final AutomationConfig config = recipe.getConfig();
        final String objectName = String.format("%s-asset", recipe.getAutomationResourceId().split("/")[1]);
        final String objectPath = format("{0}/{1}/{2}", recipe.getOrderId(), recipe.getRequestId(), objectName);

        if (config.getStorePage()) {
            String filepath = String.format("%s.html", objectPath);
            String pageSource = driver.getPageSource();
            createFile(filepath, pageSource.getBytes());
        }

        if (config.getStoreScreenshot()) {
            String filepath = String.format("%s.png", objectPath);
            TakesScreenshot ts = (TakesScreenshot) driver;
            createFile(filepath, ts.getScreenshotAs(OutputType.BYTES));
        }

        if (config.getStoreJsonResponse()) {
            String filepath = String.format("%s.json", objectPath);
            createFile(filepath, getEntityAsJson(response).getBytes());
        }
    }

    private void createFile(String filepath, byte[] bytes) throws IOException {
        // write gcs
        BlobId blobId = BlobId.of(baseBucket, filepath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, bytes);

        // write local
        String filename = filepath.split("/")[2];
        Path path = Paths.get(filename);
        Files.write(path, bytes);
    }

    protected void withDriverConfig(@NotNull WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    private String getEntityAsJson(final AutomationResponse<?> response) {
        return gson.toJson(response.getResponseEntity());
    }

    private WebDriver getDriver() {
        WebDriver driver = withRemoteWebdriver(webdriverUri, prepareHeadlessBrowser());
        withDriverConfig(driver);
        return driver;
    }
}

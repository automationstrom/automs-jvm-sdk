package app.automs.internal;

import app.automs.internal.config.StromProperties;
import app.automs.internal.domain.AutomationRecipe;
import app.automs.internal.domain.AutomationResponse;
import app.automs.internal.traits.HtmlParser;
import app.automs.internal.traits.StorageHandler;
import app.automs.internal.traits.Webdriver;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static app.automs.internal.domain.AutomationProcessingStatus.*;
import static java.text.MessageFormat.format;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
abstract public class StromAutomation implements Webdriver, HtmlParser, StorageHandler {

    @Autowired
    public StromProperties properties;
    protected WebDriver driver;
    @Autowired
    private Storage storage;
    @Autowired
    private Gson gson;

    private static byte[] takeScreenShotAsByte(WebDriver webDriver) throws IOException {
        return takeFullPageScreenShotAsByte(webDriver);
    }

    private static byte[] takeFullPageScreenShotAsByte(WebDriver webDriver) throws IOException {
        val fpScreenshot =
                new AShot()
                        .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                        .takeScreenshot(webDriver);

        val originalImage = fpScreenshot.getImage();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(originalImage, "png", baos);
            baos.flush();
            return baos.toByteArray();
        }
    }

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

            store(recipe, recipeResponse, exitPointUrl);
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
                         final @NotNull String exitPointUrl) {
        val config = recipe.getConfig();
        val objectName = String.format("%s-asset", recipe.getAutomationResourceId().split("/")[1]);
        val objectPath = format("{0}/{1}/{2}", recipe.getOrderId(), recipe.getRequestId(), objectName);

        if (config.getStorePage()) {
            val filepath = String.format("%s.html", objectPath);
            val pageSource = appendAbsolutPath(driver.getPageSource(), exitPointUrl);
            createFile(filepath, pageSource.getBytes());
        }

        if (config.getStoreScreenshot()) {
            val filepath = String.format("%s.png", objectPath);
            createFile(filepath, takeScreenShotAsByte(driver));
        }

        if (config.getStoreJsonResponse()) {
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

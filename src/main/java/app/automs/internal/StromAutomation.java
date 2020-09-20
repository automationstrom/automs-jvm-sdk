package app.automs.internal;

import app.automs.internal.domain.AutomationConfig;
import app.automs.internal.domain.AutomationRecipe;
import app.automs.internal.domain.AutomationResponse;
import app.automs.internal.traits.StromPdfHandler;
import app.automs.internal.traits.StromWebdriver;
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

import static app.automs.internal.domain.AutomationProcessingStatus.INTERNAL_ERROR;
import static app.automs.internal.domain.AutomationProcessingStatus.VALIDATION_FAIL;

abstract public class StromAutomation implements StromWebdriver, StromPdfHandler {

    @Value("${automs.automation.resourceId}")
    public String resourceId;
    protected WebDriver driver;
    @Value("${automs.webdriver.url}")
    private String webdriverUri;
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
        final String filename = recipe.getAutomationResourceId().replace("/", "-").substring(1) + "-v1";


        if (config.getStorePage()) {
            Path path = Paths.get(String.format("%s.html", filename));
            String pageSource = driver.getPageSource();
            Files.write(path, pageSource.getBytes());
        }

        if (config.getStoreScreenshot()) {
            Path path = Paths.get(String.format("%s.png", filename));
            TakesScreenshot ts = (TakesScreenshot) driver;
            Files.write(path, ts.getScreenshotAs(OutputType.BYTES));
        }

        if (config.getStoreJsonResponse()) {
            Path path = Paths.get(String.format("%s.json", filename));
            Files.write(path, asJson(response).getBytes());
        }
    }

    protected void withDriverConfig(@NotNull WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    private String asJson(final AutomationResponse<?> response) {
        return gson.toJson(response.getResponseEntity());
    }

    private WebDriver getDriver() {
        WebDriver driver = withRemoteWebdriver(webdriverUri, prepareHeadlessBrowser());
        withDriverConfig(driver);
        return driver;
    }
}


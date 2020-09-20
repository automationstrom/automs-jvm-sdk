package app.automs.internal;

import app.automs.internal.domain.AutomationConfig;
import app.automs.internal.domain.AutomationRecipe;
import app.automs.internal.domain.AutomationResponse;
import app.automs.internal.traits.StromPdfHandler;
import app.automs.internal.traits.StromWebdriver;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        driver = getDriver();
        driver.get(entryPointUrl());
        AutomationResponse<?> recipeResponse = process(recipe.getInputParams());
        driver.quit();

        if (!validate(recipeResponse)) {
            recipeResponse.setProcessingStatus(VALIDATION_FAIL);
        }

        store(recipe.getConfig(), recipeResponse);

        return recipeResponse;
    }

    protected Boolean store(@NotNull AutomationConfig config, @NotNull AutomationResponse<?> response) {
        String storePage;
        if (config.getStorePage()) {
            storePage = asJson(response);
        }

        String screenshot;
        if (config.getStoreScreenshot()) {
            screenshot = asJson(response);
        }
        return true;
    }

    protected void withDriverConfig(@NotNull WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }


    protected String asJson(final AutomationResponse<?> response) {
        return gson.toJson(response);
    }

    private WebDriver getDriver() {
        WebDriver driver = withRemoteWebdriver(webdriverUri, prepareHeadlessBrowser());
        withDriverConfig(driver);
        return driver;
    }
}


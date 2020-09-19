package app.automs.internal;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.unmodifiableMap;

abstract public class StromAutomation implements StromWebdriver, StromPdfHandler {

    @Value("${automs.automation.resourceId}")
    public String resourceId;
    protected WebDriver driver;
    @Value("${automs.webdriver.url}")
    private String webdriverUri;
    @Autowired
    private Gson gson;

    protected abstract String recipe(String... args);

    protected abstract Boolean validate(@NotNull String... args);

    protected abstract String entryPointUrl();

    public String run(String... args) {
        driver = getDriver();
        driver.get(entryPointUrl());
        String recipeResponse = recipe(args);
        driver.quit();

        if (!validate(recipeResponse)) {
            throw new IllegalStateException("automation not successfully validated");
        }

        return transform(recipeResponse);
    }

    protected String transform(@NotNull String... capturedResponses) {
        return asJson(resourceId, capturedResponses[0]);
    }

    protected void withDriverConfig(@NotNull WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    private WebDriver getDriver() {
        WebDriver driver = withRemoteWebdriver(webdriverUri, prepareHeadlessBrowser());
        withDriverConfig(driver);
        return driver;
    }

    protected String asJson(final String queryId, final String response) {
        return gson.toJson(unmodifiableMap(new LinkedHashMap<String, String>() {{
            put("uuid", UUID.randomUUID().toString());
            put("queryId", queryId);
            put("response", response);
            put("date", LocalDateTime.now().toString());
        }}));
    }
}


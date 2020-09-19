package app.automs.internal;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public interface StromWebdriver {

    Logger logger = LoggerFactory.getLogger(StromWebdriver.class);

    @NotNull
    default ChromeOptions prepareHeadlessBrowser() {
        final ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        return chromeOptions;
    }

    @NotNull
    default WebDriver withRemoteWebdriver(String remoteWebdriver, ChromeOptions chromeOptions) {
        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL(remoteWebdriver), chromeOptions);
        } catch (MalformedURLException e) {
            logger.error("error", e);
        }

        Objects.requireNonNull(driver);
        return driver;
    }
}

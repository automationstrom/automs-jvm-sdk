package app.automs.internal.traits;

import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;

public interface StromWebdriver {

    Logger log = LoggerFactory.getLogger(StromWebdriver.class);

    @NotNull
    default ChromeOptions prepareHeadlessBrowser() {
        val chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        
        return chromeOptions;
    }

    @NotNull
    @SneakyThrows
    default WebDriver withRemoteWebdriver(String remoteWebdriver, ChromeOptions chromeOptions) {
        val driver = new RemoteWebDriver(new URL(remoteWebdriver), chromeOptions);
        Objects.requireNonNull(driver);
        return driver;
    }
}

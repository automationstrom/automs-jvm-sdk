package app.automs.sdk.traits;

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

public interface Webdriver {

    Logger log = LoggerFactory.getLogger(Webdriver.class);

    @NotNull
    default ChromeOptions prepareHeadlessBrowser() {
        val chromeOptions = new ChromeOptions();

        // https://peter.sh/experiments/chromium-command-line-switches/#load-extension
        // more details

        // core
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--no-sandbox");

        // testing
        chromeOptions.addArguments("--window-size=1366,768");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-notifications");
//        chromeOptions.addArguments("--no-zygote");
//        chromeOptions.addArguments("--disable-extensions");

        //recommend
        chromeOptions.addArguments("--disable-background-timer-throttling");
        chromeOptions.addArguments("--disable-backgrounding-occluded-windows");
        chromeOptions.addArguments("--disable-breakpad");
        chromeOptions.addArguments("--disable-component-extensions-with-background-pages");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-features=TranslateUI,BlinkGenPropertyTrees");
        chromeOptions.addArguments("--disable-ipc-flooding-protection");
        chromeOptions.addArguments("--disable-renderer-backgrounding");
        chromeOptions.addArguments("--enable-features=NetworkService,NetworkServiceInProcess");
        chromeOptions.addArguments("--force-color-profile=srgb");
        chromeOptions.addArguments("--hide-scrollbars");
        chromeOptions.addArguments("--metrics-recording-only");
        chromeOptions.addArguments("--mute-audio");

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

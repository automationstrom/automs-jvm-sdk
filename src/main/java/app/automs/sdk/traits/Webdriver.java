package app.automs.sdk.traits;

import app.automs.sdk.domain.config.ChromeDriverOptionsConfig;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface Webdriver {

    Logger log = LoggerFactory.getLogger(Webdriver.class);

    @NotNull
    default ChromeOptions prepareHeadlessBrowser(final ChromeDriverOptionsConfig config) {
        val chromeOptions = new ChromeOptions();
        // more details @ https://peter.sh/experiments/chromium-command-line-switches/#load-extension
        chromeOptions
                .addArguments(Stream.of(config.getDefaultOptions(), config.getSessionOptions())
                        .flatMap(List::stream).collect(toList()));
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

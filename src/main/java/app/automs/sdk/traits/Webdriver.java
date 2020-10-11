package app.automs.sdk.traits;

import app.automs.sdk.domain.config.headless.ChromeDriverOptionsConfig;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.logging.LogType.*;
import static org.openqa.selenium.remote.CapabilityType.LOGGING_PREFS;

public interface Webdriver {

    Logger log = LoggerFactory.getLogger(Webdriver.class);

    @NotNull
    default ChromeOptions prepareHeadlessBrowser(final ChromeDriverOptionsConfig config) {
        val chromeOptions = new ChromeOptions();
        val logging = new LoggingPreferences();
        val proxy = new Proxy();

        // enforce headless and invalid ssl certs
        chromeOptions.setHeadless(true);
        chromeOptions.setAcceptInsecureCerts(true);

        // chromeOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.DISMISS_AND_NOTIFY);

        // define proxy config
        if (!config.getHttpProxy().equals("none")) {
            // selenium wrapper, some webdriver need explicit configuration,
            proxy.setHttpProxy(config.getHttpProxy());
            chromeOptions.setProxy(proxy);
            chromeOptions.addArguments("--proxy-server", config.getHttpProxy());
        }

        // define user-agent
        chromeOptions.addArguments("--user-agent", config.getCustomUserAgent());

        // merge default and client session chrome options
        chromeOptions
                .addArguments(Stream.of(config.getDefaultOptions(), config.getSessionOptions())
                        .flatMap(List::stream).collect(toList()));

        // define logging
        logging.enable(CLIENT, Level.INFO);
        logging.enable(BROWSER, Level.INFO);
        logging.enable(DRIVER, Level.INFO);

        chromeOptions.setCapability(LOGGING_PREFS, logging);

        // use tracking id for downloads handling
        chromeOptions.setCapability("browserless.trackingId", config.getTrackingId());

        return chromeOptions;
    }

    @NotNull
    @SneakyThrows
    default WebDriver withRemoteWebdriver(String remoteWebdriver, ChromeOptions chromeOptions) {
        val driver = new RemoteWebDriver(new URL(remoteWebdriver), chromeOptions);
        Objects.requireNonNull(driver);
        return driver;
    }

    @SuppressWarnings("unused")
    void setAutomationHardTimeoutLimit(@NotNull WebDriver driver, long seconds);
}

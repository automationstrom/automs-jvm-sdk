package app.automs.sdk.domain.config;

import app.automs.sdk.domain.config.store.StoreJsonResponseConfig;
import app.automs.sdk.domain.config.store.StorePageConfig;
import app.automs.sdk.domain.config.store.StoreScreenshotConfig;
import lombok.Data;

@Data
public class AutomationConfig {
    private StoreScreenshotConfig screenshotConfig = new StoreScreenshotConfig();
    private StorePageConfig pageCopyConfig = new StorePageConfig();
    private StoreJsonResponseConfig jsonResponseConfig = new StoreJsonResponseConfig();
    private ChromeDriverOptionsConfig chromeDriverOptionsConfig = new ChromeDriverOptionsConfig();
}

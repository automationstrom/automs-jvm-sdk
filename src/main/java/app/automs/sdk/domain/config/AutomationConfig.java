package app.automs.sdk.domain.config;

import app.automs.sdk.domain.config.headless.ChromeDriverOptionsConfig;
import app.automs.sdk.domain.config.store.PageScreenCaptureConfig;
import app.automs.sdk.domain.config.store.PageCopyConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class AutomationConfig {
    private PageScreenCaptureConfig pageCaptureConfig = new PageScreenCaptureConfig();
    private PageCopyConfig pageCopyConfig = new PageCopyConfig();
    private ChromeDriverOptionsConfig chromeDriverOptionsConfig = new ChromeDriverOptionsConfig();
}

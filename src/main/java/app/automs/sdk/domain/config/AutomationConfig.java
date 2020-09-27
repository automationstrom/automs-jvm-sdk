package app.automs.sdk.domain.config;

import app.automs.sdk.domain.config.store.StoreJsonResponseConfig;
import app.automs.sdk.domain.config.store.StorePageConfig;
import app.automs.sdk.domain.config.store.StoreScreenshotConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class AutomationConfig {
    private StoreScreenshotConfig screenshotConfig = new StoreScreenshotConfig();
    private StorePageConfig pageCopyConfig = new StorePageConfig();
    private StoreJsonResponseConfig jsonResponseConfig = new StoreJsonResponseConfig();

//    @JsonIgnore
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ChromeDriverOptionsConfig chromeDriverOptionsConfig = new ChromeDriverOptionsConfig();
}

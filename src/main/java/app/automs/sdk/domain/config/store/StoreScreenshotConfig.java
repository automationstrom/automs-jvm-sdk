package app.automs.sdk.domain.config.store;

import lombok.Data;

import static app.automs.sdk.domain.config.store.ScreenshotTarget.*;

@Data
public class StoreScreenshotConfig {

    private Boolean storeScreenshot = true;
    private ScreenshotTarget target = FULLPAGE;
    private String withElement = "";
    private ScreenshotElementType elementType;

}

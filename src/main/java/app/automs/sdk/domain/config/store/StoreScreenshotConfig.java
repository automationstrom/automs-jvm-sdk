package app.automs.sdk.domain.config.store;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static app.automs.sdk.domain.config.store.ScreenshotTarget.FULL_PAGE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Data
@JsonInclude(NON_EMPTY)
public class StoreScreenshotConfig {

    private Boolean storeScreenshot = true;
    private ScreenshotTarget target = FULL_PAGE;
    private String withElement = "";
    private ScreenshotElementType elementType;

}

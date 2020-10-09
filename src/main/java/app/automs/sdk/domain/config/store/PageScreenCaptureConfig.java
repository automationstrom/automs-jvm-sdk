package app.automs.sdk.domain.config.store;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static app.automs.sdk.domain.config.store.ScreenCaptureType.FULL_PAGE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Data
@JsonInclude(NON_EMPTY)
public class PageScreenCaptureConfig {

    private Boolean enableScreenCapture = true;
    private ScreenCaptureType captureType = FULL_PAGE;
    private String targetElement = "";
    private ScreenCaptureElementType elementType;

}

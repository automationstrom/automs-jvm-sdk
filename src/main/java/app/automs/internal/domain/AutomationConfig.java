package app.automs.internal.domain;

import lombok.Data;

@Data
public class AutomationConfig {
    private Boolean storeScreenshot = true;
    private Boolean storePage = true;
    private Boolean storeJsonResponse = true;
}

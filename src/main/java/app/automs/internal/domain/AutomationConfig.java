package app.automs.internal.domain;

public class AutomationConfig {
    private Boolean storeScreenshot = true;
    private Boolean storePage = true;
    private Boolean storeJsonResponse = true;

    public Boolean getStoreScreenshot() {
        return storeScreenshot;
    }

    public void setStoreScreenshot(Boolean storeScreenshot) {
        this.storeScreenshot = storeScreenshot;
    }

    public Boolean getStorePage() {
        return storePage;
    }

    public void setStorePage(Boolean storePage) {
        this.storePage = storePage;
    }

    public Boolean getStoreJsonResponse() {
        return storeJsonResponse;
    }

    public void setStoreJsonResponse(Boolean storeJsonResponse) {
        this.storeJsonResponse = storeJsonResponse;
    }
}

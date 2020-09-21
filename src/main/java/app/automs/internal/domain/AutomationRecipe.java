package app.automs.internal.domain;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;

public class AutomationRecipe {
    private final String processingTime = ZonedDateTime.now(UTC).format(ISO_INSTANT);
    private String orderId;
    private String requestId;
    private String automationResourceId;
    private Map<String, String> inputParams = Collections.emptyMap();
    private AutomationConfig config = new AutomationConfig();
    private AutomationResponse<?> response = AutomationResponse.EMPTY_RESPONSE;

    public static AutomationRecipe createFrom(AutomationRequest request) {
        AutomationRecipe recipe = new AutomationRecipe();
        recipe.setOrderId(request.getOrderId());
        recipe.setRequestId(request.getRequestId());
        recipe.setInputParams(request.getInputParams());
        recipe.setAutomationResourceId(request.getAutomationResourceId());
        return recipe;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAutomationResourceId() {
        return automationResourceId;
    }
    public String getResourceIdParsed() {
        return automationResourceId.replace("/", "-").substring(1);
    }

    public void setAutomationResourceId(String automationResourceId) {
        this.automationResourceId = automationResourceId;
    }

    public Map<String, String> getInputParams() {
        return inputParams;
    }

    public void setInputParams(Map<String, String> inputParams) {
        this.inputParams = inputParams;
    }

    public AutomationResponse<?> getResponse() {
        return response;
    }

    public void setResponse(AutomationResponse<?> response) {
        this.response = response;
    }

    public AutomationConfig getConfig() {
        return config;
    }

    public void setConfig(AutomationConfig config) {
        this.config = config;
    }

    public String getProcessingTime() {
        return processingTime;
    }

}

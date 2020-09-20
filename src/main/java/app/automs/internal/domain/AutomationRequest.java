package app.automs.internal.domain;

import java.util.Map;

public class AutomationRequest {
    private String automationResourceId;
    private String orderId;
    private String requestId;
    private Map<String, String> inputParams;

    public String getAutomationResourceId() {
        return automationResourceId;
    }

    public void setAutomationResourceId(String automationResourceId) {
        this.automationResourceId = automationResourceId;
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

    public Map<String, String> getInputParams() {
        return inputParams;
    }

    public void setInputParams(Map<String, String> inputParams) {
        this.inputParams = inputParams;
    }
}

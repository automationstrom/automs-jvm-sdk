package app.automs.sdk.domain.http;

import lombok.Data;

import java.util.Map;

@Data
public class AutomationRequest {
    private String automationResourceId;
    private String orderId;
    private String requestId;
    private Map<String, String> inputParams;
}

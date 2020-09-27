package app.automs.sdk.domain.http;

import app.automs.sdk.domain.config.AutomationConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
public class AutomationRequest {
    private String automationResourceId;
    private String orderId;
    private String requestId;
    private Map<String, String> inputParams;

    @JsonInclude(NON_NULL)
    private AutomationConfig config = new AutomationConfig();
}

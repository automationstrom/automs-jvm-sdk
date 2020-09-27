package app.automs.sdk.domain;

import app.automs.sdk.domain.http.AutomationRequest;
import app.automs.sdk.domain.http.AutomationResponse;
import app.automs.sdk.domain.config.AutomationConfig;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;

@Data
public class AutomationRecipe {
    private final String processingTime = ZonedDateTime.now(UTC).format(ISO_INSTANT);
    private String orderId;
    private String requestId;
    private String automationResourceId;
    private Map<String, String> inputParams = Collections.emptyMap();
    private AutomationConfig config = new AutomationConfig();
    private AutomationResponse<?> response = AutomationResponse.EMPTY_RESPONSE;

    @NotNull
    public static AutomationRecipe createFrom(AutomationRequest request) {
        val recipe = new AutomationRecipe();
        recipe.setOrderId(request.getOrderId());
        recipe.setRequestId(request.getRequestId());
        recipe.setInputParams(request.getInputParams());
        recipe.setAutomationResourceId(request.getAutomationResourceId());
        recipe.config = request.getConfig();
        return recipe;
    }

}

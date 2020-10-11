package app.automs.sdk.domain;

import app.automs.sdk.domain.config.AutomationConfig;
import app.automs.sdk.domain.http.AutomationInput;
import app.automs.sdk.domain.http.AutomationRequest;
import app.automs.sdk.domain.http.AutomationResponse;
import lombok.Data;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;

@Data
public class AutomationRecipe {
    private final String processingTime = ZonedDateTime.now(UTC).format(ISO_INSTANT);
    private String orderId;
    private String requestId;
    private String automationResourceId;
    private AutomationInput automationInput = new AutomationInput();
    private AutomationConfig config = new AutomationConfig();
    private AutomationResponse<?> response = AutomationResponse.EMPTY_RESPONSE;

    @SuppressWarnings("unused")
    @NotNull
    public static AutomationRecipe createFrom(@NotNull AutomationRequest request) {
        val recipe = new AutomationRecipe();
        recipe.setOrderId(request.getOrderId());
        recipe.setRequestId(request.getRequestId());
        recipe.setAutomationInput(AutomationInput.create(request.getInputParams()));
        recipe.setAutomationResourceId(request.getAutomationResourceId());
        recipe.config = request.getConfig();
        // enforce tracking id equals to request id
        recipe.config.getChromeDriverOptionsConfig().setTrackingId(request.getRequestId());
        return recipe;
    }

}

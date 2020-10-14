package app.automs.sdk.traits;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationInput;
import app.automs.sdk.domain.http.AutomationResponse;
import app.automs.sdk.domain.http.AutomationValidation;
import org.jetbrains.annotations.NotNull;

public interface AutomationFunction {

    AutomationResponse<?> process(@NotNull AutomationInput automationInput);

    @SuppressWarnings("unused")
    AutomationResponse<?> run(@NotNull AutomationRecipe recipe);

    AutomationValidation validate(@NotNull AutomationResponse<?> response);

    @NotNull
    String entryPointUrl();

}

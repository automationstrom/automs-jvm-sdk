package app.automs.sdk.traits;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationInput;
import app.automs.sdk.domain.http.AutomationResponse;
import app.automs.sdk.domain.http.AutomationValidation;
import app.automs.sdk.domain.http.ResponseOutput;
import org.jetbrains.annotations.NotNull;

public interface AutomationFunction {

    AutomationResponse<? extends ResponseOutput> process(@NotNull AutomationInput automationInput);

    @SuppressWarnings("unused")
    AutomationResponse<? extends ResponseOutput> run(@NotNull AutomationRecipe recipe);

    AutomationValidation validate(@NotNull AutomationResponse<? extends ResponseOutput> response);

    @NotNull
    String entryPointUrl();

}

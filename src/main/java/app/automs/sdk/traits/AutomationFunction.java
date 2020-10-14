package app.automs.sdk.traits;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationInput;
import app.automs.sdk.domain.http.AutomationResponse;
import app.automs.sdk.domain.http.AutomationValidation;
import app.automs.sdk.domain.http.ResponseOutput;
import org.jetbrains.annotations.NotNull;

public interface AutomationFunction<T extends ResponseOutput> {

    @NotNull AutomationResponse<? extends ResponseOutput> process(@NotNull AutomationInput automationInput);

    @SuppressWarnings("unused")
    @NotNull AutomationResponse<T> run(@NotNull AutomationRecipe recipe);

    AutomationValidation validate(@NotNull AutomationResponse<T> response);

    @NotNull
    String entryPointUrl();

}

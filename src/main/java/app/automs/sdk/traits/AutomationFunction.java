package app.automs.sdk.traits;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationInput;
import app.automs.sdk.domain.http.AutomationResponse;
import org.jetbrains.annotations.NotNull;

public interface AutomationFunction {

    AutomationResponse<?> process(@NotNull AutomationInput automationInput);

    @SuppressWarnings("unused")
    AutomationResponse<?> run(@NotNull AutomationRecipe recipe);

    Boolean validate(@NotNull AutomationResponse<?> response);

    @NotNull
    String entryPointUrl();

}

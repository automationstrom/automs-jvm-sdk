package app.automs.sdk.traits;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface AutomationFunction {

    AutomationResponse<?> process(@NotNull Map<String, String> args);

    AutomationResponse<?> run(@NotNull AutomationRecipe recipe);

    Boolean validate(@NotNull AutomationResponse<?> response);

    @NotNull
    String entryPointUrl();

}

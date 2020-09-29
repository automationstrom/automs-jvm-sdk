package app.automs.sdk.traits;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationResponse;

public interface Function {
    AutomationResponse<?> run(AutomationRecipe recipe);
}

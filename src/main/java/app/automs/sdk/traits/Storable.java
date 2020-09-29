package app.automs.sdk.traits;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationResponse;
import org.jetbrains.annotations.NotNull;

public interface Storable {
    void store(final @NotNull AutomationRecipe recipe,
               final @NotNull AutomationResponse<?> response,
               final @NotNull String exitPointUrl,
               final @NotNull String givenPageSource,
               final @NotNull String objectPath);
}

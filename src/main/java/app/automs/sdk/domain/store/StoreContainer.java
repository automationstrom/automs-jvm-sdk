package app.automs.sdk.domain.store;

import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class StoreContainer {
    private @NotNull
    AutomationRecipe recipe;
    private @NotNull
    AutomationResponse<?> response;
    private @NotNull
    String exitPointUrl;
    private @NotNull
    String givenPageSource;
    private @NotNull
    String objectPath;
}

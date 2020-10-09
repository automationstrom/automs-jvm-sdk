package app.automs.sdk.domain.store;

import app.automs.sdk.domain.config.AutomationConfig;
import app.automs.sdk.domain.http.AutomationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class StoreContainer {
    private @NotNull
    AutomationConfig config;
    private @NotNull
    AutomationResponse<?> response;
    private @NotNull
    String exitPointUrl;
    private @NotNull
    String givenPageSource;
    private @NotNull
    String objectPath;
}

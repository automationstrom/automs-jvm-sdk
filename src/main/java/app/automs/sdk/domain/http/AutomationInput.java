package app.automs.sdk.domain.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class AutomationInput {
    @NotNull
    private Map<String, String> inputParams = Collections.emptyMap();

    @JsonIgnore
    private Promise<?> completable;

    public static AutomationInput create(Map<String, String> params) {
        val automationInput = new AutomationInput();
        automationInput.setInputParams(params);
        return automationInput;
    }

    public static AutomationInput create(Promise<?> element, Map<String, String> params) {
        val automationInput = new AutomationInput();
        automationInput.setCompletable(element);
        automationInput.setInputParams(params);
        return automationInput;
    }
}

package app.automs.sdk.domain.http;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AutomationValidation {
    Boolean isValid;
    List<String> errorMessages;
}

package app.automs.internal.domain;

import lombok.Data;

@Data
public class AutomationResponse<T> {
    @SuppressWarnings("rawtypes")
    public static final AutomationResponse EMPTY_RESPONSE = new AutomationResponse<>();
    private AutomationProcessingStatus processingStatus = AutomationProcessingStatus.PROCESSING;
    private String messageResponse = "";
    private T responseEntity;
}

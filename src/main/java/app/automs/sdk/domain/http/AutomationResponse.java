package app.automs.sdk.domain.http;

import app.automs.sdk.domain.AutomationProcessingStatus;
import app.automs.sdk.domain.store.AdditionalFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class AutomationResponse<T> {
    @SuppressWarnings("rawtypes")
    public static final AutomationResponse EMPTY_RESPONSE = new AutomationResponse<>();
    private AutomationProcessingStatus processingStatus = AutomationProcessingStatus.PROCESSING;
    private String messageResponse = "";
    private T responseEntity;
    @JsonIgnore
    private List<AdditionalFile> additionalFile = Collections.emptyList();
}

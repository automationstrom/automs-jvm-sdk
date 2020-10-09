package app.automs.sdk.domain.http;

import app.automs.sdk.domain.AutomationProcessingStatus;
import app.automs.sdk.domain.store.SessionFile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Data
@JsonInclude(NON_EMPTY)
public class AutomationResponse<T> {
    @SuppressWarnings("rawtypes")
    public static final AutomationResponse EMPTY_RESPONSE = new AutomationResponse<>();
    private AutomationProcessingStatus processingStatus = AutomationProcessingStatus.PROCESSING;
    private String customResponse = "";
    private T responseEntity;
    private List<SessionFile> sessionFiles = Collections.emptyList();
}

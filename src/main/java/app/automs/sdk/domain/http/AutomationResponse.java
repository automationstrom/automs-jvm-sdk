package app.automs.sdk.domain.http;

import app.automs.sdk.domain.AutomationState;
import app.automs.sdk.domain.store.SessionFile;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
@SuppressWarnings("unused")
final public class AutomationResponse<T extends ResponseOutput> {
    final private T responseEntity;
    final private AutomationState status;
    private String customResponse = "";
    private List<SessionFile> sessionFiles = Collections.emptyList();

    public AutomationResponse(@NotNull T responseEntity) {
        this.responseEntity = responseEntity;
        this.status = AutomationState.PROCESSING;
    }

    private AutomationResponse(@NotNull T responseEntity, AutomationState status, String customResponse,
                               List<SessionFile> sessionFiles) {
        this.responseEntity = responseEntity;
        this.status = status;
        this.customResponse = customResponse;
        this.sessionFiles = sessionFiles;
    }

    public static AutomationResponse<? extends ResponseOutput> withEmptyOutput() {
        return new AutomationResponse<>(EmptyResponseOutput.emptyOutput());
    }

    public AutomationResponse<? extends ResponseOutput> withStatus(AutomationState status) {
        return (this.status == status) ? this :
                new AutomationResponse<>(this.responseEntity, status, this.customResponse, this.sessionFiles);
    }

    public AutomationState getStatus() {
        return status;
    }

    public @NotNull T getResponseEntity() {
        return responseEntity;
    }

    public String getCustomResponse() {
        return customResponse;
    }

    public void setCustomResponse(String customResponse) {
        this.customResponse = customResponse;
    }

    public List<SessionFile> getSessionFiles() {
        return sessionFiles;
    }

    public void setSessionFiles(List<SessionFile> sessionFiles) {
        this.sessionFiles = sessionFiles;
    }
}

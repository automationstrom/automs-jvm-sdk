package app.automs.internal.domain;

public class AutomationResponse<T> {
    @SuppressWarnings("rawtypes")
    public static final AutomationResponse EMPTY_RESPONSE = new AutomationResponse<>();
    private AutomationProcessingStatus processingStatus = AutomationProcessingStatus.PROCESSING;
    private String messageResponse = "";

    private T responseEntity;

    public T getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(T responseEntity) {
        this.responseEntity = responseEntity;
    }

    public AutomationProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(AutomationProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getMessageResponse() {
        return messageResponse;
    }

    public void setMessageResponse(String messageResponse) {
        this.messageResponse = messageResponse;
    }
}

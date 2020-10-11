package app.automs.sdk.domain.config.store;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@SuppressWarnings("unused")
@Data
@JsonInclude(NON_NULL)
public class PageStructuredConfig {
    private Boolean storeStructuredResponse = true;
}

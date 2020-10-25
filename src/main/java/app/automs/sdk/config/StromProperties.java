package app.automs.sdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@SuppressWarnings("SpringFacetCodeInspection")
@Data
@Configuration
@ConfigurationProperties(prefix = "automs")
public class StromProperties {

    @NotBlank
    private String resourceId;
    @NotBlank
    private String webdriver;
    @NotBlank
    private String webdriverFallback;
    @NotBlank
    private String baseBucket;
    @NotBlank
    private String projectId;
    @NotBlank
    private String responsePublisherApi;
}

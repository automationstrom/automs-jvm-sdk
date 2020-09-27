package app.automs.internal.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties(prefix = "automs")
public class StromProperties {

    @NotBlank
    private String resourceId;
    @NotBlank
    private String webdriver;
    @NotBlank
    private String baseBucket;
    @NotBlank
    private String projectId;
}

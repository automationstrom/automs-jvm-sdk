package app.automs.internal.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Configuration
@ConfigurationProperties(prefix = "automs")
public class StromProperties {
    private String resourceId;
    private String webdriver;
    private String baseBucket;
    private String projectId;
}

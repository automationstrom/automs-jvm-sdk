package app.automs.internal.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class StromConfig {

    @Autowired
    public StromProperties properties;

    @Bean
    public Storage createStorage() {
        val context = System.getenv("CONTEXT") == null ? "local" : System.getenv("CONTEXT");

        Storage storage;
        if (context.equalsIgnoreCase("local") || context.equalsIgnoreCase("compose")) {
            storage = LocalStorageHelper.getOptions().getService();
        } else if (context.equalsIgnoreCase("cloud")) {
            storage = StorageOptions.newBuilder().setProjectId(properties.getProjectId()).build().getService();
        } else {
            throw new IllegalArgumentException(
                    String.format("invalid given env var [CONTEXT=%s] ", context)
            );
        }
        return storage;
    }
}

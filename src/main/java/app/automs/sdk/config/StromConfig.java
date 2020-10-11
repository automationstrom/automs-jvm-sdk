package app.automs.sdk.config;

import app.automs.sdk.domain.config.EnvContext;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;
import lombok.val;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("ALL")
public class StromConfig {

    public final StromProperties properties;

    public StromConfig(StromProperties properties) {
        this.properties = properties;
    }

    @Bean
    public EnvContext envContext() {
        return new EnvContext(System.getenv("CONTEXT") == null ? "local" : System.getenv("CONTEXT"));
    }

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient().newBuilder().build();
    }

    @Bean
    public Storage createStorage() {
        val context = envContext();

        Storage storage;
        if (context.isDev()) {
            storage = LocalStorageHelper.getOptions().getService();
        } else if (context.isCloud()) {
            storage = StorageOptions.newBuilder().setProjectId(properties.getProjectId()).build().getService();
        } else {
            throw new IllegalArgumentException(
                    String.format("invalid given env var [CONTEXT=%s] ", context)
            );
        }
        return storage;
    }
}

package app.automs.internal;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StromConfig {

    @Value("${automs.projectId}")
    public String projectId;

    @Bean
    public Storage createStorage() {
//        Storage storage = LocalStorageHelper.getOptions().getService();
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        return storage;
    }

}

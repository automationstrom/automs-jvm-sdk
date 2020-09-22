package app.automs.internal;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StromConfig {

    @Value("${automs.projectId}")
    public String projectId;

    @Bean
    public Storage createStorage() {
//        val storage = LocalStorageHelper.getOptions().getService();
        val storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        return storage;
    }

}

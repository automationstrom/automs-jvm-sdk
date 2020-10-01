package app.automs.sdk;

import app.automs.sdk.config.StromProperties;
import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.http.AutomationResponse;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;

@Component
public class StromStorage {

    @Autowired
    public StromProperties properties;
    @Autowired
    private Storage storage;
    @Autowired
    private Gson gson;


    @SneakyThrows
    public void createFile(String filepath, byte[] bytes) {
        // write gcs
        val blobId = BlobId.of(properties.getBaseBucket(), filepath);
        val blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, bytes);
    }

    public void createFile(String filepath, @NotNull AutomationResponse<?> response) {
        createFile(filepath, gson.toJson(response.getResponseEntity()).getBytes());
    }

    @NotNull
    public String parseStoragePath(@NotNull AutomationRecipe recipe) {
        val objectName = String.format("%s-asset", recipe.getAutomationResourceId().split("/")[1]);
        return format("{0}/{1}/{2}", recipe.getOrderId(), recipe.getRequestId(), objectName);
    }

}

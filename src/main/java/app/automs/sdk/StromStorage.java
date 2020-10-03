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

import java.nio.file.Files;
import java.nio.file.Paths;

import static java.text.MessageFormat.format;
import static java.time.Instant.now;

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

        // write local
        val context = System.getenv("CONTEXT") == null ? "local" : System.getenv("CONTEXT");
        if (context.equalsIgnoreCase("local") || context.equalsIgnoreCase("compose")) {
            val filename = filepath.split("/")[2];
            val instant = now().getEpochSecond();
            val path = Paths.get(String.format("%d-%s", instant, filename));
            Files.write(path, bytes);
        }
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

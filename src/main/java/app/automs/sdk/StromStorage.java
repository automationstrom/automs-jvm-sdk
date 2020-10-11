package app.automs.sdk;

import app.automs.sdk.config.StromProperties;
import app.automs.sdk.domain.AutomationRecipe;
import app.automs.sdk.domain.config.EnvContext;
import app.automs.sdk.domain.http.AutomationResponse;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.gson.Gson;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

import static java.text.MessageFormat.format;
import static java.time.Instant.now;


@Component
public class StromStorage {

    public final StromProperties properties;
    private final Storage storage;
    private final EnvContext context;
    private final Gson gson;

    public StromStorage(StromProperties properties, Storage storage, EnvContext context, Gson gson) {
        this.properties = properties;
        this.storage = storage;
        this.context = context;
        this.gson = gson;
    }


    @SneakyThrows
    public void createFile(String filepath, byte[] bytes) {
        // write gcs
        val blobId = BlobId.of(properties.getBaseBucket(), filepath);
        val blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, bytes);

        // write local and strip file path
        val filename = filepath.split("/")[2];
        if (context.isLocal()) {
            Files.write(Paths.get(parseDevPath(filename)), bytes);
        } else if (context.isCompose()) {
            Files.write(Paths.get(filename), bytes);
        }
    }

    private String parseDevPath(String filename) {
        val splitFileName = filename.split("asset");
        val instant = now().getEpochSecond();
        return String.format("%s%d-asset%s", splitFileName[0], instant, splitFileName[1]);
    }

    public void createFile(String filepath, @NotNull AutomationResponse<?> response) {
        createFile(filepath, gson.toJson(response.getResponseEntity()).getBytes());
    }

    @NotNull
    public String parseStoragePath(@NotNull AutomationRecipe recipe) {
        val objectName = String.format("%s-asset", recipe.getAutomationResourceId().split("/")[1]);
        return format("{0}/{1}/{2}", recipe.getOrderId(), recipe.getRequestId(), objectName);
    }

    // TODO better error handling, if 404 throw error?
    public byte[] getDownloadedFile(String endpoint, String requestId, String filename) {
        return Unirest.get(
                format("{0}/{1}/{2}",
                        String.format("%s/workspace", endpoint.replace("/webdriver", "")),
                        requestId,
                        filename)
        ).asBytes().getBody();
    }

}

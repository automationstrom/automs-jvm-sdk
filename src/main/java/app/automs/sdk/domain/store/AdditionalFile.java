package app.automs.sdk.domain.store;

import lombok.Data;
import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;

import static app.automs.sdk.domain.store.StoreExtension.NONE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.entity.ContentType.DEFAULT_BINARY;

@Data
public class AdditionalFile {
    final private String label = "udf";
    final private Charset charset = UTF_8;
    final private ContentType contentType = DEFAULT_BINARY;
    final private StoreExtension extension = NONE;
    private byte[] content;
}

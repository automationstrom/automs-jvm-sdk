package app.automs.sdk.domain.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.val;
import org.apache.http.entity.ContentType;

import static app.automs.sdk.domain.store.StoreExtension.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.entity.ContentType.DEFAULT_BINARY;

@SuppressWarnings("unused")
@Data
@JsonInclude(NON_NULL)
public class SessionFile {
    private String label = "udf";
    private ContentType contentType = ContentType.create(DEFAULT_BINARY.getMimeType(), UTF_8);
    private StoreExtension extension = NONE;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] content;

    public static SessionFile create(byte[] content) {
        val af = new SessionFile();
        af.setContent(content);
        return af;
    }

    public static SessionFile create(byte[] content, StoreExtension extension) {
        val af = new SessionFile();
        af.setContent(content);
        af.setExtension(extension);
        return af;
    }

    public static SessionFile create(byte[] content, StoreExtension extension, ContentType contentType) {
        val af = new SessionFile();
        af.setContent(content);
        af.setExtension(extension);
        af.setContentType(contentType);
        return af;
    }

}

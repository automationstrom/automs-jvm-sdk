package app.automs.sdk.domain.store;

@SuppressWarnings("unused")
public enum StoreExtension {
    PDF(".pdf"),
    HTML(".html"),
    JPEG(".jpeg"),
    PNG(".png"),
    GIF(".gif"),
    XML(".xml"),
    TEXT_PLAIN(".txt"),
    JSON(".json"),
    NONE("");

    private final String extension;

    StoreExtension(String extension) {
        this.extension = extension;
    }

    public String getValue() {
        return this.extension;
    }
}

package app.automs.sdk.domain.store;

public enum StoreExtension {
    PDF(".pdf"),
    HTML(".html"),
    JPEG(".jpeg"),
    XML(".xml"),
    TEXT_PLAIN(".txt"),
    NONE("");

    private final String extension;

    StoreExtension(String extension) {
        this.extension = extension;
    }

    public String getValue() {
        return this.extension;
    }
}

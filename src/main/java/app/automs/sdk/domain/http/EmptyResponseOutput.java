package app.automs.sdk.domain.http;

@SuppressWarnings("unused")
final public class EmptyResponseOutput implements ResponseOutput {
    private static final EmptyResponseOutput instance;

    static {
        instance = new EmptyResponseOutput();
    }

    private EmptyResponseOutput() {
    }

    public static EmptyResponseOutput emptyOutput() {
        return instance;
    }
}

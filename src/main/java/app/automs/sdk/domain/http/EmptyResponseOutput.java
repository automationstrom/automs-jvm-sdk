package app.automs.sdk.domain.http;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
final public class EmptyResponseOutput implements ResponseOutput {
    private static final EmptyResponseOutput instance;

    static {
        instance = new EmptyResponseOutput();
    }

    private EmptyResponseOutput() {
    }

    @NotNull
    public static EmptyResponseOutput emptyOutput() {
        return instance;
    }
}

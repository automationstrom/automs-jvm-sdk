package app.automs.sdk.traits;

import app.automs.sdk.domain.store.StoreContainer;
import org.jetbrains.annotations.NotNull;

public interface Storable {
    @SuppressWarnings("unused")
    void store(final @NotNull StoreContainer container);
}

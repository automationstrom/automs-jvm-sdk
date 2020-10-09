package app.automs.sdk.traits;

import app.automs.sdk.domain.store.StoreContainer;
import org.jetbrains.annotations.NotNull;

public interface Storable {
    void store(final @NotNull StoreContainer container);
}

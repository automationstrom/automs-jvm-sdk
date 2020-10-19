package app.automs.sdk.domain.http;

import lombok.val;

@SuppressWarnings("unused")
public class Promise<T> {
    private T value;

    public static <T> Promise<T> of(T e) {
        val defElement = new Promise<T>();
        defElement.setValue(e);
        return defElement;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

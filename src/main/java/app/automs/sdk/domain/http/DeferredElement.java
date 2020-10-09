package app.automs.sdk.domain.http;

import lombok.val;

public class DeferredElement<T> {
    private T element;

    public static <T> DeferredElement<T> create(T e) {
        val defElement = new DeferredElement<T>();
        defElement.setElement(e);
        return defElement;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }
}

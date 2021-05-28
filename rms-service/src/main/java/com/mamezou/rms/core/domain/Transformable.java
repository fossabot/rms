package com.mamezou.rms.core.domain;

import java.util.function.Function;

public interface Transformable {
    @SuppressWarnings("unchecked")
    default <T, R> R transform(Function<T, R> func) {
        return func.apply((T) this);
    }
}

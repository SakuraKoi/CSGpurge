package dev.sakurakooi.CSGPurge.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NullableOptional<T> {
    public boolean has;
    public T value;
    public static <T> NullableOptional<T> of(T value) {
        NullableOptional<T> nullableOptional = new NullableOptional<>(true, value);
        nullableOptional.has = true;
        nullableOptional.value = value;
        return nullableOptional;
    }

    public static <T> NullableOptional<T> empty() {
        return new NullableOptional<>(false, null);
    }

    public boolean isPresent() {
        return has;
    }

    public boolean isNull() {
        return value == null;
    }

    public T get() {
        return value;
    }
}

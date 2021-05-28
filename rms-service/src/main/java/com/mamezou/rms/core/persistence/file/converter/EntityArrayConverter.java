package com.mamezou.rms.core.persistence.file.converter;

import com.mamezou.rms.core.exception.RmsSystemException;

public interface EntityArrayConverter<T> {

    T toEntity(String[] attributes) throws RmsSystemException;

    String[] toArray(T entity);
}

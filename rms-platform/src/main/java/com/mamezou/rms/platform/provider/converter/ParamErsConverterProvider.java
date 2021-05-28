package com.mamezou.rms.platform.provider.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.eclipse.microprofile.config.ConfigProvider;

// Provider Class
// register by @RegisterProvider or Application#getClasseses()
public class ParamErsConverterProvider implements ParamConverterProvider {

    private final String pattern;

    public ParamErsConverterProvider() {
        // ConfigがなぜかInjectで取得できないためProvierクラス経由で取得
        this.pattern = ConfigProvider.getConfig().getValue("json.format.date", String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == LocalDate.class) {
            return (ParamConverter<T>) new LocalDateConverter();
        }
        return null;
    }


    // ----------------------------------------------------- inner classes

    public class LocalDateConverter implements ParamConverter<LocalDate> {
        @Override
        public LocalDate fromString(String value) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
        }
        @Override
        public String toString(LocalDate value) {
            //return value == null ? null : value.format(DateTimeFormatter.ofPattern(pattern));
            return value == null ? "" : value.format(DateTimeFormatter.ofPattern(pattern));
        }
    }
}

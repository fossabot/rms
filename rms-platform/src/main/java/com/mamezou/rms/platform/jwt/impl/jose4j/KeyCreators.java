package com.mamezou.rms.platform.jwt.impl.jose4j;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.function.Function;

import org.jose4j.keys.HmacKey;

class KeyCreators {
    static final Function<String, Key> PHRASE_TO_KEY_CONVERTER = (phrase) -> {
        try {
            return new HmacKey(phrase.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    };
}

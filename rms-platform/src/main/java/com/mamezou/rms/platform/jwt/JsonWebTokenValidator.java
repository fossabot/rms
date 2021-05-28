package com.mamezou.rms.platform.jwt;

import org.eclipse.microprofile.jwt.JsonWebToken;

public interface JsonWebTokenValidator {
    JsonWebToken validate(String token) throws JwtValidateException;
}

package com.mamezou.rms.external.webapi.jwt;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaimsFactory;

@Dependent
public class UserClaimsFactoryProducers {

    @Produces
    public UserClaimsFactory createFactory() {
        return new UserDtoClaimsFactory();
    }
}

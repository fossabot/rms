package com.mamezou.rms.external.webapi.jwt;

import com.mamezou.rms.external.webapi.dto.UserAccountResourceDto;
import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaims;
import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaimsFactory;

public class UserDtoClaimsFactory implements UserClaimsFactory {

    @Override
    public boolean canNewInstanceFrom(Object obj) {
        return obj instanceof UserAccountResourceDto;
    }

    @Override
    public UserClaims newInstanceFrom(Object obj) {
        return new UserClaimsAdaptor((UserAccountResourceDto)obj);
    }
}

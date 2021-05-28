package com.mamezou.rms.external.webapi.jwt;

import java.util.Set;

import com.mamezou.rms.external.webapi.dto.UserAccountResourceDto;
import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaims;

public class UserClaimsAdaptor implements UserClaims {

    private UserAccountResourceDto org;

    UserClaimsAdaptor(UserAccountResourceDto org) {
        this.org = org;
    }

    @Override
    public String getUserId() {
        return String.valueOf(org.getId());
    }
    @Override
    public String getUserPrincipalName() {
        return org.getContact() + "@rms.com";
    }
    @Override
    public Set<String> getGroups() {
        return Set.of(org.getUserType());
    }
}

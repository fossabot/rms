package com.mamezou.rms.platform.jwt.filter;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class JwtSecurityFilterFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(JwtSupplyResponseFilter.class);
        context.register(JwtSecurityRequestFilter.class);
        return true;
    }
}

package com.mamezou.rms.platform.provider.converter;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

// register by @RegisterProvider or Application#getClasseses()
public class RmsTypeParameterFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(JsonbErsConfig.class);
        context.register(ParamErsConverterProvider.class);
        return true;
    }
}

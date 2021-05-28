package com.mamezou.rms.external.webapi.mapper;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.mamezou.rms.external.webapi.mapper.ServerExceptionMappers.BusinessFlowExceptionMapper;
import com.mamezou.rms.external.webapi.mapper.ServerExceptionMappers.ConstraintExceptionMapper;
import com.mamezou.rms.external.webapi.mapper.ServerExceptionMappers.RmsSystemExceptionMapper;
import com.mamezou.rms.platform.provider.mapper.PageNotFoundExceptionMapper;
import com.mamezou.rms.platform.provider.mapper.UnhandledExceptionMapper;

public class ServerExceptionMapperFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(BusinessFlowExceptionMapper.class);
        context.register(RmsSystemExceptionMapper.class);
        context.register(ConstraintExceptionMapper.class);
        context.register(PageNotFoundExceptionMapper.class);
        context.register(UnhandledExceptionMapper.class);
        return true;
    }
}

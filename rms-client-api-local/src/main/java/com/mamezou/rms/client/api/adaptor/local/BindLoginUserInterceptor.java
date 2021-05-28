package com.mamezou.rms.client.api.adaptor.local;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.mamezou.rms.client.api.adaptor.local.BindLoginUser.LoginAction;
import com.mamezou.rms.client.api.dto.UserAccountClientDto;
import com.mamezou.rms.core.common.LoginUserUtils;
import com.mamezou.rms.core.common.ServiceLoginUser;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@BindLoginUser
public class BindLoginUserInterceptor {

    private ServiceLoginUser currentLoginUser;

    @AroundInvoke
    public Object obj(InvocationContext ic) throws Exception {
        if (ic.getMethod().isAnnotationPresent(LoginAction.class)) {
            return invokeWithKeepLoginUser(ic);
        }
        return invokeWithBindLoginUser(ic);
    }

    private Object invokeWithKeepLoginUser(InvocationContext ic) throws Exception {
        var result = ic.proceed();
        if (result instanceof UserAccountClientDto) {
            var userAccountDto = (UserAccountClientDto) result;
            currentLoginUser = ServiceLoginUser.of(userAccountDto.getId(), userAccountDto.getRoles());
        }
        return result;
    }

    private Object invokeWithBindLoginUser(InvocationContext ic) throws Exception {
        try {
            LoginUserUtils.set(currentLoginUser);
            return ic.proceed();
        } finally {
            LoginUserUtils.remove();
        }
    }
}

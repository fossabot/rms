package com.mamezou.rms.external.webapi.jwt;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.mamezou.rms.core.common.LoginUserUtils;
import com.mamezou.rms.core.common.ServiceLoginUser;
import com.mamezou.rms.platform.jwt.filter.JwtSecurityRequestFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * 検証済み{@link JsonWebToken}から{@link ServiceLoginUser}を生成し<code>ThreadLocal</code>
 * に設定するフィルタークラス。
 * このフィルターは前段に{@link JwtSecurityRequestFilter}が実行されていることを
 * 前提にしている。
 */
@Priority(Priorities.AUTHENTICATION + 10) // JwtSecurityRequestFilterの後
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class LoginUserRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var jsonWebToken = (JsonWebToken) requestContext.getSecurityContext().getUserPrincipal();
        ServiceLoginUser loginUser = ServiceLoginUser.UNKNOWN_USER;
        if (jsonWebToken != null) {
            loginUser = ServiceLoginUser.of(Integer.parseInt(jsonWebToken.getSubject()), jsonWebToken.getGroups());
        }
        log.debug("set loginUser to ThradLocal");
        LoginUserUtils.set(loginUser);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.debug("remove loginUser from ThradLocal");
        LoginUserUtils.remove();
    }
}

package com.mamezou.rms.platform.jwt.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.Range;

import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator;
import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaims;
import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaimsFactory;
import com.mamezou.rms.platform.jwt.JwtConfig;

import lombok.extern.slf4j.Slf4j;

@GenerateToken
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class JwtSupplyResponseFilter implements ContainerResponseFilter {

    private static Range<Integer> SUCCESS_STATUS = Range.between(200, 299);

    private UserClaimsFactory userClaimsFactory;
    private JsonWebTokenGenerator tokenGenerator;

    @Inject
    public JwtSupplyResponseFilter(UserClaimsFactory factory, JsonWebTokenGenerator generator) {
        this.userClaimsFactory = factory;
        this.tokenGenerator = generator;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        // 例外が発生した場合はExecptionMapperでレスポンスが設定されているので
        // まずはHTTPステータスを見て成功か失敗かを判定
        if (!SUCCESS_STATUS.contains(responseContext.getStatus())) {
            return;
        }

        if (!responseContext.hasEntity()) {
            log.warn("ボディが設定されていません");
            return;
        }

        Object entity = responseContext.getEntity();
        if (!userClaimsFactory.canNewInstanceFrom(entity)) {
            log.warn("ボディのインスタンスが想定外です [class={}]", entity.getClass().getName());
            return;
        }

        // JwtTokenの生成
        UserClaims userClaims = userClaimsFactory.newInstanceFrom(entity);
        String jwtToken = tokenGenerator.generateToken(userClaims);
        log.info("Generated JWT-Token=>[{}]", jwtToken); // ホントはログに書いちゃダメだけどネ

        var headers = responseContext.getHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, JwtConfig.BEARER_MARK + " " + jwtToken);
    }
}

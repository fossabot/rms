package com.mamezou.rms.platform.jwtrole;

import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.platform.jwt.JsonWebTokenFilterTest.TestUserClaims;
import com.mamezou.rms.platform.jwt.JsonWebTokenFilterTest.TestUserClaimsFactory;
import com.mamezou.rms.platform.jwt.filter.Authenticated;
import com.mamezou.rms.platform.jwt.filter.GenerateToken;
import com.mamezou.rms.platform.jwt.filter.JwtSecurityFilterFeature;
import com.mamezou.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator;
import com.mamezou.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator;
import com.mamezou.rms.platform.jwtrole.JwtRoleSecurityFilterIntegrationTest.JwtRoleTestApplication;
import com.mamezou.rms.platform.role.RoleSecurityDynamicFeature;
import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "server.port", value = "7001")
@AddBean(JwtRoleTestApplication.class)
@AddBean(TestUserClaimsFactory.class)
@AddBean(Jose4jJwtGenerator.class)
@AddBean(Jose4PrivateSecretedTokenValidator.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
public class JwtRoleSecurityFilterIntegrationTest {


    private static String authHeaderValue;

    @AfterEach
    void tearDown() {
        authHeaderValue = null;
    }

    // ?????????????????????????????????
    @Test
    void testUnAuthenticateScenario() throws Exception {

        UnAuthenticateScenarioTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(UnAuthenticateScenarioTestApi.class);

        // ????????????????????????????????????OK
        endPoint.noRestriction();

        // ????????????@Authenticated&@RolesAllowed ???NG?????????????????????
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.authAndRolesAnnotated(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());

        // ????????????@RolesAllowed ???NG?????????????????????
        actual = catchThrowableOfType(() ->
            endPoint.rolesAnnotatedOnly(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }

    // ?????????????????????????????????
    @Test
    void testAuthenticatedScenario() throws Exception {

        AuthenticatedScenarioTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(AuthenticatedScenarioTestApi.class);

        // ???????????????
        endPoint.login();

        // ?????????@Authenticated?????????OK
        endPoint.authAnnotatedOnly();

        // ?????????@Authenticated&@DenyAll ??? NG
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.authAndDenyAllAnnotated(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());

        // ?????????@Authenticated&@RolesAllowed(???????????????) ??? OK
        endPoint.authAndRolesAllowedAnnotatedOk();
        actual = catchThrowableOfType(() ->
            endPoint.authAndRolesAllowedAnnotatedNg(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }

    // ?????????????????????????????????
    @Path("/test1")
    @RegisterProvider(JwtRoleSenderClientResponseFilter.class)
    public interface UnAuthenticateScenarioTestApi {

        // ????????????????????????????????????OK
        @GET
        @Path("/noRestriction")
        @Produces(MediaType.TEXT_PLAIN)
        String noRestriction();

        // ????????????@Authenticated&@RolesAllowed ???NG?????????????????????
        @GET
        @Path("/authAndRolesAnnotated")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndRolesAnnotated();

        // ????????????@RolesAllowed ???NG?????????????????????
        @GET
        @Path("/rolesAnnotatedOnly")
        @Produces(MediaType.TEXT_PLAIN)
        String rolesAnnotatedOnly();

    }

    // ?????????????????????????????????
    @Path("/test2")
    @RegisterProvider(JwtRoleSenderClientResponseFilter.class)
    public interface AuthenticatedScenarioTestApi {

        // ????????????
        @GET
        @Path("/login")
        @Produces(MediaType.APPLICATION_JSON)
        TestUserClaims login();

        // ?????????@Authenticated?????????OK
        @GET
        @Path("/authAnnotatedOnly")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAnnotatedOnly();

        // ?????????@Authenticated&@DenyAll ??? NG
        @GET
        @Path("/authAndDenyAllAnnotated")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndDenyAllAnnotated();

        // ?????????@Authenticated&@RolesAllowed(???????????????) ??? OK
        @GET
        @Path("/authAndRolesAllowedAnnotatedOk")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndRolesAllowedAnnotatedOk();

        // ?????????@Authenticated&@RolesAllowed(???????????????) ??? NG(???????????????)
        @GET
        @Path("/authAndRolesAllowedAnnotatedNg")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndRolesAllowedAnnotatedNg();

        default String authHeaderValue() {
            return authHeaderValue;
        }

    }

    @Path("/test1")
    public static class UnAuthenticateScenarioTestResource implements UnAuthenticateScenarioTestApi {

        @Override
        public String noRestriction() {
            return "success";
        }

        @Authenticated
        @RolesAllowed("roleX")
        @Override
        public String authAndRolesAnnotated() {
            return "success";
        }

        @RolesAllowed("roleX")
        @Override
        public String rolesAnnotatedOnly() {
            return "success";
        }
    }

    public static class AuthenticatedScenarioTestResource implements AuthenticatedScenarioTestApi {

        @GenerateToken
        @Override
        public TestUserClaims login() {
            return new TestUserClaims();
        }

        @Authenticated
        @Override
        public String authAnnotatedOnly() {
            return "success";
        }

        @Authenticated
        @DenyAll
        @Override
        public String authAndDenyAllAnnotated() {
            return "success";
        }

        @Authenticated
        @RolesAllowed("roleA")
        @Override
        public String authAndRolesAllowedAnnotatedOk() {
            return "success";
        }

        @Authenticated
        @RolesAllowed("roleX")
        @Override
        public String authAndRolesAllowedAnnotatedNg() {
            return "success";
        }

    }

    // Register by @AddBenan
    public static class JwtRoleTestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        UnAuthenticateScenarioTestResource.class,
                        AuthenticatedScenarioTestResource.class,
                        JwtSecurityFilterFeature.class,
                        RoleSecurityDynamicFeature.class
                    );
        }
    }

    // Register by @RegisterProvider
    public static class JwtRoleSenderClientResponseFilter implements ClientResponseFilter {

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            if (!responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("Authorization??????");
                return;
            }
            JwtRoleSecurityFilterIntegrationTest.authHeaderValue = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        }
    }
}

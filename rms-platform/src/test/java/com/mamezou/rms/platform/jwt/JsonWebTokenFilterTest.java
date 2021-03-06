package com.mamezou.rms.platform.jwt;

import static com.mamezou.rms.platform.jwt.JsonWebTokenFilterTest.SecurityFilterTestApi.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.platform.jwt.JsonWebTokenFilterTest.TestApplication;
import com.mamezou.rms.platform.jwt.JsonWebTokenFilterTest.TestUserClaimsFactory;
import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaims;
import com.mamezou.rms.platform.jwt.JsonWebTokenGenerator.UserClaimsFactory;
import com.mamezou.rms.platform.jwt.filter.Authenticated;
import com.mamezou.rms.platform.jwt.filter.GenerateToken;
import com.mamezou.rms.platform.jwt.filter.JwtSecurityFilterFeature;
import com.mamezou.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator;
import com.mamezou.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator;
import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@AddConfig(key = "server.port", value = "7001")
@AddBean(TestApplication.class)
@AddBean(TestUserClaimsFactory.class)
@AddBean(Jose4jJwtGenerator.class)
@AddBean(Jose4PrivateSecretedTokenValidator.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
public class JsonWebTokenFilterTest {

    private SecurityFilterTestApi endPoint;
    private static String authHeaderValue;

    // ----------------------------------------------------- lifecycle methods

    @BeforeEach
    void setup() throws Exception {
        this.endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(SecurityFilterTestApi.class);
    }

    @AfterEach
    void tearDown() {
        authHeaderValue = null;
    }

    // ----------------------------------------------------- test methods

    @Test
    void testJwtAuthSuccessSequence() {
        // ??????????????????????????????????????????JWT??????????????????
        endPoint.login(SUCCESS);
        // ???????????????JWT????????????????????????????????????????????????????????????????????????????????????
        endPoint.secure(SUCCESS);
        // ???????????????JWT???????????????????????????????????????????????????????????????????????????????????????
        endPoint.unsecure(SUCCESS);
    }

    @Test
    void testJwtAuthComplexSequence() {

        // ???????????????????????????JWT?????????????????????
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.login(ERROR),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());

        // JWT???????????????????????????????????????
        actual = catchThrowableOfType(() ->
            endPoint.secure(SUCCESS),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());

        // ????????????????????????????????????JWT??????????????????
        endPoint.login(SUCCESS);
        // ?????????JWT???????????????????????????????????????
        endPoint.secure(SUCCESS);

        // ??????????????????????????????????????????JWT????????????????????????????????????
        actual = catchThrowableOfType(() ->
            endPoint.login(ERROR),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
        endPoint.secure(SUCCESS);
    }

    @Test
    @AddConfig(key = "jwt.filter.enable", value = "false")
    void testFilterOff() {
        endPoint.secure(SUCCESS);
    }

    // ----------------------------------------------------- test mock classes

    @Path("/test")
    @RegisterProvider(JwtSenderClientResponseFilter.class)
    public interface SecurityFilterTestApi {

        static final String SUCCESS = "success";
        static final String ERROR = "error";

        @GET
        @Path("/login")
        @Produces(MediaType.APPLICATION_JSON)
        TestUserClaims login(@QueryParam("pttn") String pttn);

        @GET
        @Path("/secure")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String secure(@QueryParam("pttn") String pttn);

        @GET
        @Path("/unsecure")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String unsecure(@QueryParam("pttn") String pttn);

        default String authHeaderValue() {
            return authHeaderValue;
        }
    }

    // Register by @AddBenan
    public static class TestApplication extends Application {
        // RequestFilter???ResponseFilter?????????Provider???@AddBean??????????????????????????????
        // Application#getClasses???????????????????????????????????????Application#getClasses???
        // ??????????????????????????????Resource??????????????????????????????????????????????????????Resource????????????
        // ????????????????????????????????????
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        SecurityFilterTestResourceImpl.class,
                        JwtSecurityFilterFeature.class
                    );
        }
    }

    // Register by @AddBenan
    @Path("/test")
    public static class SecurityFilterTestResourceImpl implements SecurityFilterTestApi {

        @GenerateToken
        @Override
        public TestUserClaims login(String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return new TestUserClaims();
        }

        @Authenticated
        @RolesAllowed("roleA")
        @Override
        public String secure(String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return "success";
        }

        @Authenticated
        @Override
        public String unsecure(String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return "success";
        }
    }

    // Register by @AddBenan
    public static class TestUserClaimsFactory implements UserClaimsFactory {
        @Override
        public boolean canNewInstanceFrom(Object obj) {
            return (obj instanceof TestUserClaims);
        }
        @Override
        public UserClaims newInstanceFrom(Object obj) {
            return (TestUserClaims) obj;
        }
    }

    // Register by @RegisterProvider
    public static class JwtSenderClientResponseFilter implements ClientResponseFilter {
        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            if (!responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("Authorization??????");
                return;
            }
            JsonWebTokenFilterTest.authHeaderValue = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        }
    }

    // POJO
    public static class TestUserClaims implements UserClaims {
        @Override
        public String getUserId() {
            return "test";
        }
        @Override
        public String getUserPrincipalName() {
            return "test@test";
        }
        @Override
        public Set<String> getGroups() {
            return Set.of("roleA");
        }
    }
}

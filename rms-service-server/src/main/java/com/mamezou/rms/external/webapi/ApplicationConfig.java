package com.mamezou.rms.external.webapi;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import com.mamezou.rms.external.webapi.jwt.LoginUserRequestFilter;
import com.mamezou.rms.external.webapi.mapper.GenericErrorInfo;
import com.mamezou.rms.external.webapi.mapper.ServerExceptionMapperFeature;
import com.mamezou.rms.external.webapi.mapper.ValidationErrorInfo;
import com.mamezou.rms.platform.jwt.filter.JwtSecurityFilterFeature;
import com.mamezou.rms.platform.log.ServerHeaderDumpFilter;
import com.mamezou.rms.platform.provider.converter.RmsTypeParameterFeature;
import com.mamezou.rms.platform.role.RoleSecurityDynamicFeature;

/**
 * RESTアプリケーションのコンフィグ情報。
 * REST API全体に関することをOpenAPIのアノテーションで定義している。
 */
@OpenAPIDefinition(
        info = @Info(title = "レンタル予約システムの公開API", version = "0.0.1-SNAPSHOT",
            contact = @Contact(name = "豆ちゃん", url = "https://www.mamezou.com")),
        components = @Components(
                schemas = {
                    @Schema(
                            name = "localDateTime",
                            description = "日時型",
                            format = "yyyyMMdd HH:mm",
                            example = "20210314 09:00",
                            implementation = LocalDateTime.class
                            )
                },
                responses = {
                    @APIResponse(
                            name = "NotFound",
                            responseCode = "404",
                            description = "該当データがない場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "ServerError",
                            responseCode = "500",
                            description = "アプリケーション内部でエラーが発生した場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のRmsSystemExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "Forbidden",
                            responseCode = "403",
                            description = "対象データに対する操作権限がない場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "UnknownData",
                            responseCode = "404",
                            description = "処理対象データが存在しない場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "DataDupricate",
                            responseCode = "409",
                            description = "登録データが既に登録されている",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のBusinessFlowExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericErrorInfo.class))
                            ),
                    @APIResponse(
                            name = "ParameterError",
                            responseCode = "400",
                            description = "パラメータエラーの場合",
                            headers = @Header(name = "Rms-Exception", description = "発生例外のConstraintViolationExceptionが設定される", required = true,
                                schema = @Schema(implementation = String.class)),
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorInfo.class))
                            )
                })
        )
@ApplicationScoped
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                    RentalReservationResource.class,
                    ManagementResource.class,
                    RmsTypeParameterFeature.class,
                    ServerExceptionMapperFeature.class,
                    JwtSecurityFilterFeature.class,
                    RoleSecurityDynamicFeature.class,
                    LoginUserRequestFilter.class,
                    ServerHeaderDumpFilter.class
                );
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of(
                    // The following keys are defined in `ServerProperties.BV_SEND_ERROR_IN_RESPONSE`
                    "jersey.config.beanValidation.disable.server", true  // jerseyのJAX-RSのBeanValidationサポートをOFFにする
                );
    }
}

package com.mamezou.rms.client.api.adaptor.remote.rest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import com.mamezou.rms.client.api.adaptor.remote.auth.SecurityConstraintClientException;
import com.mamezou.rms.client.api.exception.BusinessFlowClientException;
import com.mamezou.rms.client.api.exception.UnknownClientException;
import com.mamezou.rms.client.api.exception.ValidateClientException;
import com.mamezou.rms.client.api.exception.ValidateClientException.ValidationErrorMessage;

@Priority(Priorities.USER)
public class ClientExceptionMapper implements ResponseExceptionMapper<RuntimeException> {

    private static final String RMS_EXCEPTION_HEADER = "Rms-Exception";
    private static final String SERVER_VALIDATION_ERROR = "ConstraintViolationException";
    private static final List<Integer> SECURITY_ERROR_STATUS =
            List.of(
                Status.UNAUTHORIZED.getStatusCode(),
                Status.FORBIDDEN.getStatusCode()
            );
    private static final Map<String, Function<String, RuntimeException>> DEFAULT_EXCEPTION_MAPPING =
            Map.of(
                "BusinessFlowException", BusinessFlowClientException::new
            );
    private static final Function<String, RuntimeException> UNKNOWN_ERROR_HANDLER = UnknownClientException::new;

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return headers.containsKey(RMS_EXCEPTION_HEADER)
                || SECURITY_ERROR_STATUS.contains(status);
    }

    @Override
    public RuntimeException toThrowable(Response response) {

        // StatusCodeが401か403だったらセキュリティエラー
        if (SECURITY_ERROR_STATUS.contains(response.getStatus())) {
            return toSecurityException(response);
        }

        // 例外クラス名がConstraintViolationExceptionならバリデーションエラー
        var causeClassName = response.getHeaderString(RMS_EXCEPTION_HEADER);
        if (SERVER_VALIDATION_ERROR.equals(causeClassName)) {
            return toValidationException(response);
        }

        // デフォルトマッピングのエラー
        return toDefualtMappingException(response);
    }

    private SecurityConstraintClientException toSecurityException(Response response) {
        return new SecurityConstraintClientException(response);
    }

    private ValidateClientException toValidationException(Response response) {
        ValidationErrorMessage errorMessage = response.readEntity(ValidationErrorMessage.class);
        return new ValidateClientException(errorMessage);
    }

    private RuntimeException toDefualtMappingException(Response response) {
        String causeClassName = response.getHeaderString(RMS_EXCEPTION_HEADER);
        String causeMessage = response.readEntity(new GenericType<Map<String, String>>() {}).get("errorMessage");
        return DEFAULT_EXCEPTION_MAPPING.getOrDefault(causeClassName, UNKNOWN_ERROR_HANDLER).apply(causeMessage);
    }
}

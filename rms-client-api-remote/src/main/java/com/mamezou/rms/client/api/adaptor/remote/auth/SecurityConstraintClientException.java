package com.mamezou.rms.client.api.adaptor.remote.auth;

import javax.ws.rs.core.Response;

import com.mamezou.rms.client.api.exception.RentalReservationClientException;

public class SecurityConstraintClientException extends RentalReservationClientException {

    private final transient Response response;

    public SecurityConstraintClientException(Response response) {
        super(getMessage(response));
        this.response = response;
    }

    public int getErrorStatus() {
        return response.getStatus();
    }

    private static String getMessage(Response response) {
        switch (response.getStatus()) {
            case 401:
                return "認証エラー";
            case 403:
                return "認可エラー";
            default:
                return "不明のエラー";
        }
    }
}

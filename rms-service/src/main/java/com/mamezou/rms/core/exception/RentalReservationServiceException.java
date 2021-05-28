package com.mamezou.rms.core.exception;

/**
 * RMSで捕捉、送出した{@link BusinessFlowException}と{@link RmsSystemException}を共通的に
 * ハンドルするための基底例外クラス
 */
public abstract class RentalReservationServiceException extends RuntimeException {

    public RentalReservationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RentalReservationServiceException(String message) {
        super(message);
    }

    public RentalReservationServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}

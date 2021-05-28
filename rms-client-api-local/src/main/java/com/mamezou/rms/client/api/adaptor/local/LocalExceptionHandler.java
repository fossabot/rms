package com.mamezou.rms.client.api.adaptor.local;

import com.mamezou.rms.client.api.exception.BusinessFlowClientException;
import com.mamezou.rms.client.api.exception.RmsSystemClientException;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.core.exception.RmsSystemException;

public class LocalExceptionHandler {

    public static void throwConvertedException(RuntimeException e) {
        if (e instanceof RmsSystemException) {
            throw new RmsSystemClientException(e);
        }
        if (e instanceof BusinessFlowException) {
            throw new BusinessFlowClientException(e);
        }
        throw new RmsSystemClientException(e);
    }

    public static void throwConvertedException(Exception e) {
        throw new RmsSystemClientException(e);
    }
}

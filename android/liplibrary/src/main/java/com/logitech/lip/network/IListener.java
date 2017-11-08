/*
 * Copyright (c) 2015 Logitech, Inc. All Rights Reserved
 *
 * This program is a trade secret of LOGITECH, and it is not to be reproduced,
 * published, disclosed to others, copied, adapted, distributed or displayed
 * without the prior written authorization of LOGITECH.
 *
 * Licensee agrees to attach or embed this notice on all copies of the program
 * including partial copies or modified versions thereof.
 */

package com.logitech.lip.network;

/**
 * Base Interface for the Network requests
 * @param <T>
 */
public interface IListener <T>{

    enum  ErrorCode {
        NETWORK_TIME_OUT(1000),
        NETWORK_NO_CONNECTION(1001),
        NETWORK_ERROR(1002),
        NETWORK_AUTHUNTICATION_ERROR(1003),
        PARSE_ERROR(1004),
        SERVER_ERROR(1005),
        SERVER_INVALID_ACCESS_TOKEN(1006),
        SERVER_ACCOUNT_ALREADY_EXISTS(1007),
        SERVER_ACCOUNT_NO_EMAIL(1008),
        SERVER_ACCOUNT_LOCKED(1009),
        ERROR_CODE_INTERNAL(1010),
        ERROR_CODE_USER_CANCEL(1011),
        ERROR_NULL_REFRESH_TOKEN(1012);

        final int errorCode;
        ErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }
    }

    /**
     * Called on Main thread
     * @param result  network response
     */
    void onSuccess(T result);

    /**
     * Called on Main thread
     * @param errorCode  network Error code
     * @param errorMessage  Detail error Message
     */
    void onError(IListener.ErrorCode errorCode, String errorMessage);
}

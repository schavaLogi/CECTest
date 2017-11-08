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


import com.logitech.lip.ILogger;
import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.R;
import com.logitech.lip.volley.AuthFailureError;
import com.logitech.lip.volley.NetworkError;
import com.logitech.lip.volley.NetworkResponse;
import com.logitech.lip.volley.NoConnectionError;
import com.logitech.lip.volley.ParseError;
import com.logitech.lip.volley.Response;
import com.logitech.lip.volley.ServerError;
import com.logitech.lip.volley.TimeoutError;
import com.logitech.lip.volley.VolleyError;

/**
 * Abstract class for Network response handler ,
 * The response callback will be provided on Main thread
 *
 * @param <T>
 */
public abstract class ResponseListener<T> implements Response.Listener<T>,
        Response.ErrorListener,
        IListener<T> {

    @Override
    public void onErrorResponse(VolleyError error) {
        ErrorCode errorCode = ErrorCode.ERROR_CODE_INTERNAL;
        int erroResId = R.string.lip_sign_up_error_toast_internal_error;
        if(!(error instanceof ServerError)) {
            Logger.error(ILogger.LIP_UNDEFINED,"ResponseListener", "onErrorResponse",
                    error.getMessage(), error);
        }
        if (error instanceof NoConnectionError) {
            if(error.getMessage().equalsIgnoreCase("java.io.IOException: Received authentication challenge is null") ||
                    error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found")) {
                //Server not providing header for WWW-Authenticate so change error code to match in client side
                errorCode = ErrorCode.NETWORK_NO_CONNECTION;
                erroResId = R.string.lip_sign_up_error_toast_incorrect_cred;
            } else {
                //Network connection not present
                errorCode = ErrorCode.NETWORK_NO_CONNECTION;
                erroResId = R.string.lip_sign_up_error_toast_no_network;
            }
        } else if (error instanceof NetworkError) {
            //Network error while processing request
            errorCode = ErrorCode.NETWORK_ERROR;
            erroResId = R.string.lip_sign_up_error_toast_no_network;
        } else if (error instanceof ServerError) {
            // Server responded with error
            errorCode = ErrorCode.SERVER_ERROR;

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                int statusCode = response.statusCode;
                if(statusCode == 409){
                    errorCode = ErrorCode.SERVER_ACCOUNT_ALREADY_EXISTS;
                    erroResId = R.string.lip_sign_up_error_toast_email_exists;
                } else if(statusCode == 412) {
                    errorCode = ErrorCode.PARSE_ERROR;
                    erroResId = R.string.lip_sign_up_error_input_invalid;
                } else if(statusCode == 423) {
                    errorCode = ErrorCode.SERVER_ACCOUNT_LOCKED;
                    erroResId = R.string.lip_sign_up_error_toast_account_locked;
                }else if(statusCode == 404) {
                    errorCode = ErrorCode.SERVER_ACCOUNT_NO_EMAIL;
                    erroResId = R.string.lip_sign_up_error_toast_no_email;
                } else {
                    String errorMsg = new String(response.data);
                    Logger.error(ILogger.LIP_UNDEFINED,
                            "ResponseListener", "onErrorResponse ", "errorMessage=" + errorMsg, error);
                }
            }

        } else if (error instanceof AuthFailureError) {
            errorCode = ErrorCode.NETWORK_AUTHUNTICATION_ERROR;
            erroResId = R.string.lip_sign_up_error_toast_incorrect_cred;
        } else if (error instanceof ParseError) {
            //Server Error couldn't be able to parse
            errorCode = ErrorCode.PARSE_ERROR;
            erroResId = R.string.lip_sign_up_error_toast_internal_error;
        } else if (error instanceof TimeoutError) {
            //Network time out happened
            errorCode = ErrorCode.NETWORK_TIME_OUT;
            erroResId = R.string.lip_sign_up_error_toast_timeout;
        }
        String errorMessage = LIPSdk.getContext().getString(erroResId);
        onError(errorCode, errorMessage);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void onResponse(T response) {
        if(response == null){
            response = (T) new Object();
        }
        onSuccess(response);
    }

}

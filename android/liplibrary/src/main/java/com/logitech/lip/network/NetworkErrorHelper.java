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

import android.content.Context;

import com.logitech.lip.volley.AuthFailureError;
import com.logitech.lip.volley.NetworkError;
import com.logitech.lip.volley.NetworkResponse;
import com.logitech.lip.volley.NoConnectionError;
import com.logitech.lip.volley.ParseError;
import com.logitech.lip.volley.ServerError;
import com.logitech.lip.volley.TimeoutError;
import com.logitech.lip.volley.VolleyError;


final class NetworkErrorHelper {

    private static boolean isNetworkProblem(VolleyError error) {
        return (error instanceof NoConnectionError) || (error instanceof NetworkError);
    }

    private static boolean isServerProblem(VolleyError error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    public static String getMessage(VolleyError error, Context context) {

        if (error instanceof TimeoutError) {
        }
        else if( error instanceof ParseError) {

        }
        else if (isServerProblem(error)) {
            return handleServerError(error);
        }
        else if (isNetworkProblem(error)) {

        }
        return null;
    }

    private static String handleServerError(VolleyError error) {
        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:
                    try {
                        // server might return error in structural format we can use JSON
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        }
        return null;
    }
}

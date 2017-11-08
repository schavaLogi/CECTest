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

import com.logitech.lip.LIPSdk;
import com.logitech.lip.volley.Request;
import com.logitech.lip.volley.RequestQueue;
import com.logitech.lip.volley.toolbox.Volley;

/**
 * This will create the Queue to handle network requests.
 * provide functionality to cancel all pending requests
 */
final class NetworkManager {

    private static NetworkManager INSTANCE ;
    private RequestQueue requestQueue;

    private final Context context;
    private NetworkManager(){
        context = LIPSdk.getContext();
    }

    public static NetworkManager getInstance() {
        LIPSdk.isInitialized();
        if( INSTANCE == null ) {
            synchronized (NetworkManager.class) {
                if( INSTANCE == null ) {
                    INSTANCE = new NetworkManager();
                }
            }
        }
        return INSTANCE;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Object tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}

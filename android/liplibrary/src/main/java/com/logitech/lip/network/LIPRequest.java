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


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.logitech.lip.ILogger;
import com.logitech.lip.Logger;
import com.logitech.lip.volley.AuthFailureError;
import com.logitech.lip.volley.NetworkResponse;
import com.logitech.lip.volley.ParseError;
import com.logitech.lip.volley.Request;
import com.logitech.lip.volley.Response;
import com.logitech.lip.volley.VolleyError;
import com.logitech.lip.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LIPRequest<T> extends Request<T> {

    private static final String TAG = LIPRequest.class.getSimpleName();

    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final String postBody;
    private final Response.Listener<T> listener;
    private final String reqUrl;

    /**
     * Supported request methods.
     */
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param method Http request type { @link Method }
     * @param headers Map of request headers
     * @param postBody post params
     * @param listener  callback listener
     */
    public LIPRequest(String url, Class<T> clazz, int method ,Map<String, String> headers,
                       String postBody,
                       ResponseListener<T> listener) {
        super(method, url, listener);
        this.reqUrl = url;
        this.clazz = clazz;
        this.headers = headers;
        this.postBody = postBody;
        this.listener = listener;
        setShouldCache(false);
    }

    public LIPRequest(String url, Class<T> clazz, int method ,
                       Map<String, String> headers,
                       ResponseListener<T> listener) {
        super(method, url, listener);
        this.reqUrl = url;
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.postBody = null;
        setShouldCache(false);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    public byte[] getBody() throws AuthFailureError {
        if (postBody != null) {
            return postBody.getBytes();
        }
        return null;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return new HashMap<>();
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    protected void deliverResponse(T response) {
        if(listener!= null) {
            listener.onResponse(response);
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            Logger.debug(TAG, "parseNetworkResponse", "Req Url=" + reqUrl + "Network Response =" + json );

            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Logger.error(ILogger.LIP_E001, TAG, "parseNetworkResponse", "UnsupportedEncodingException =" + e.getMessage() , e);
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            Logger.error(ILogger.LIP_E001, TAG, "parseNetworkResponse", "JsonSyntaxException =" + e.getMessage() , e);
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Will add request to the Request Queue and will process in network thread
     * Response will be giving on UI Thread
     *
     */
    public void execute() {
        NetworkManager.getInstance().addToRequestQueue(this,null);
    }
}

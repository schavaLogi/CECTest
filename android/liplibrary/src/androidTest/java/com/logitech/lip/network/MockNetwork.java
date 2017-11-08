/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.logitech.lip.network;


import com.logitech.lip.volley.Network;
import com.logitech.lip.volley.NetworkResponse;
import com.logitech.lip.volley.Request;
import com.logitech.lip.volley.ServerError;
import com.logitech.lip.volley.VolleyError;

import java.util.Collections;

public class MockNetwork implements Network {
    public boolean ALWAYS_THROW_EXCEPTIONS = false;
    private byte[] mDataToReturn = null;
    private VolleyError volleyError;

    public void setExceptionToThrow(VolleyError error) {
        ALWAYS_THROW_EXCEPTIONS = true;
        volleyError = error;
    }

    public void setDataToReturn(byte[] data) {
        mDataToReturn = data;
    }

    public Request<?> requestHandled = null;

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        if (ALWAYS_THROW_EXCEPTIONS) {
            throw volleyError;
        }
        requestHandled = request;
        return new NetworkResponse(200,mDataToReturn, Collections.<String, String>emptyMap(),
                false, 0);
    }

}

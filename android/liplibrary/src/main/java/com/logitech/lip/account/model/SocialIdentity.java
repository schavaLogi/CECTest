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

package com.logitech.lip.account.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class SocialIdentity {
    @Expose
    private String provider;

    @SerializedName("id_token")
    @Expose
    private String idToken;

    @SerializedName("access_token")
    @Expose
    private String accessToken;


    public SocialIdentity(String provider, String accessToken) {
        this(provider, accessToken, null);
    }

    public SocialIdentity(String provider, String accessToken, String idToken) {
        this.provider = provider;
        this.accessToken = accessToken;
        this.idToken = idToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    public String getIdToken() {
        return idToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() {
        return accessToken;
    }
}

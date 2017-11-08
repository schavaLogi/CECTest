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

import com.google.gson.annotations.SerializedName;


public class TokenResponse {

    @SerializedName("id_token")
    private String idToken;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private String expireTime;

    @SerializedName("email_verified")
    private boolean isEmailVerified;

    @SerializedName("resetPassword")
    private boolean isPasswordReset;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    private boolean isPasswordReset() {
        return isPasswordReset;
    }

    public void setIsPasswordReset(boolean isPasswordReset) {
        this.isPasswordReset = isPasswordReset;
    }

    public AccountToken getAccountToken() {
        long  expTime = System.currentTimeMillis() + Long.parseLong(expireTime) * 1000;
        return new AccountToken(idToken, accessToken, expTime , isEmailVerified,isPasswordReset());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenResponse)) return false;

        TokenResponse that = (TokenResponse) o;

        if (!idToken.equals(that.idToken)) return false;
        return accessToken.equals(that.accessToken) && expireTime.equals(that.expireTime);

    }

    @Override
    public int hashCode() {
        int result = idToken.hashCode();
        result = 31 * result + accessToken.hashCode();
        result = 31 * result + expireTime.hashCode();
        return result;
    }
}

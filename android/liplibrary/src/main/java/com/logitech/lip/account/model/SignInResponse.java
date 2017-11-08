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

public class SignInResponse extends TokenResponse {

    @SerializedName("refresh_token")
    private String refreshToken;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignInResponse)) return false;
        if (!super.equals(o)) return false;

        SignInResponse that = (SignInResponse) o;

        return refreshToken.equals(that.refreshToken);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + refreshToken.hashCode();
        return result;
    }
}

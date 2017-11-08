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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountToken implements Parcelable {

    @SerializedName("id_token")
    @Expose
    private String idToken;

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("email_verified")
    @Expose(serialize = false)
    private boolean isEmailVerified;

    @Expose(serialize = false)
    private long expireTime;

    @SerializedName("resetPassword")
    @Expose(serialize = false)
    private boolean isPasswordReset;

    private SocialIdentity social;

    public AccountToken(String idToken, String accessToken) {
        this.idToken = idToken;
        this.accessToken = accessToken;
    }
    public AccountToken(String idToken, String accessToken,long expireTime, boolean isEmailVerified,boolean isPasswordReset ) {
        this(idToken,accessToken);
        this.expireTime = expireTime;
        this.isEmailVerified = isEmailVerified;
        this.isPasswordReset = isPasswordReset;
    }
    public AccountToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public boolean isPasswordReset() {
        return isPasswordReset;
    }

    public void setIsPasswordReset(boolean isPasswordReset) {
        this.isPasswordReset = isPasswordReset;
    }

    public SocialIdentity getSocial() {
        return social;
    }

    public void setSocial(SocialIdentity social) {
        this.social = social;
    }

    AccountToken(Parcel parcel) {
        accessToken = parcel.readString();
        idToken = parcel.readString();
        expireTime = parcel.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accessToken);
        dest.writeString(idToken);
        dest.writeLong(expireTime);
    }

    public static final Parcelable.Creator<AccountToken> CREATOR = new Parcelable.Creator<AccountToken>() {

        @Override
        public AccountToken createFromParcel(Parcel source) {
            return new AccountToken(source);
        }

        @Override
        public AccountToken[] newArray(int size) {
            return new AccountToken[size];
        }
    };

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + accessToken.hashCode();
        result = result * 31 + idToken.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AccountToken)) {
            return false;
        }

        AccountToken other = (AccountToken) o;
        return accessToken.equals(other.accessToken) &&
                idToken.equals(other.idToken);
    }
}

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

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.logitech.lip.profile.SocialProfile;

import org.json.JSONObject;

public class UserInfo extends BaseRequest {

    @SerializedName("channel_id")
    @Expose
    private String channelId;

    private String sub;

    @SerializedName("grant_type")
    @Expose
    private String grantType;

    @Expose
    private String email;

    @Expose
    private String password;

    @SerializedName("email_status")
    @Expose(serialize = false)
    private boolean emailStatus;

    @Expose
    private boolean create;

    @SerializedName("verify_email")
    @Expose
    private boolean verifyEmail;

    @SerializedName("given_name")
    @Expose
    private String firstName;

    @SerializedName("family_name")
    @Expose
    private String lastName;
    @Expose
    private String picture;
    @Expose
    private Address address;
    @Expose
    private Location location;

    @SerializedName("locale")
    @Expose
    private String language;

    @SerializedName("zoneinfo")
    @Expose
    private String zoneinfo;

    @Expose
    private SocialIdentity social;

    @SerializedName("email_verified")
    @Expose(serialize = false)
    private boolean isEmailVerified;

    // To avoid serialization and deserialization
    private transient boolean isPersist;

    private transient JSONObject actualObject;

    public UserInfo(String email, String password, boolean isCreate) {
        this.email = email;
        this.password = password;
        this.setCreate(isCreate);
    }

    public UserInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserInfo(SocialProfile profile, SocialIdentity identity) {
        this.social = identity;
        if (profile != null) {
            setEmail(profile.email);
            setFirstName(profile.firstName);
            setLastName(profile.lastName);
            // setLastName(profile.getName());

            if(!TextUtils.isEmpty(profile.getImgUrl())) {
                setPicture(profile.getImgUrl());
            }
        }
    }
    public UserInfo(SocialIdentity social) {
        this(null, social);
    }

    public UserInfo(SocialProfile profile) {
        this(profile, null);
    }

    public String getUid() {
        return sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(boolean emailStatus) {
        this.emailStatus = emailStatus;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isVerifyEmail() {
        return verifyEmail;
    }

    public void setVerifyEmail(boolean verifyEmail) {
        this.verifyEmail = verifyEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCountry() {
        if(address != null) {
            return address.country;
        }
        return null;
    }

    public void setCountry(String country) {
        if(address == null) {
            address = new Address();
        }
        address.country = country;

    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public SocialIdentity getSocialIdentity() {
        return social;
    }

    public void setSocial(SocialIdentity social) {
        this.social = social;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public SocialIdentity getSocial() {
        return social;
    }


    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public boolean isPersist() {
        return isPersist;
    }

    public void setIsPersist(boolean isPersist) {
        this.isPersist = isPersist;
    }

    public JSONObject getActualObject() {
        return actualObject;
    }

    public void setActualObject(JSONObject actualObject) {
        this.actualObject = actualObject;
    }

    private class Address {
        private String country;
    }

    private class Location {
        private String longitude;
        private String latitude;
    }
}

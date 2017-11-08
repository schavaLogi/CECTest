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

public class ChangePassword extends BaseRequest {

    private String email;

    @SerializedName("old_password")

    private String oldPassword;

    @SerializedName("new_password")
    private String newPassword;

    private SocialIdentity social;

    public ChangePassword(String email, String oldPassword, String newPassword) {
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public ChangePassword(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public ChangePassword(String email, SocialIdentity social, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
        this.social = social;
    }
	
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public SocialIdentity getSocial() {
        return social;
    }

    public void setSocial(SocialIdentity social) {
        this.social = social;
    }
}

package com.logitech.lip.account.model;

import com.google.gson.annotations.SerializedName;

/**
 * Will hold data required to change basic information like email , first name and lastname
 * To change email we need to pass new email other fields are ignored
 * To change first name and last name we should not pass email
 */
public class ChangeClaims extends BaseRequest {
    private String email;

    @SerializedName("given_name")
    private String firstName;

    @SerializedName("family_name")
    private String lastName;

    private SocialIdentity social;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public SocialIdentity getSocial() {
        return social;
    }

    public void setSocial(SocialIdentity social) {
        this.social = social;
    }
}

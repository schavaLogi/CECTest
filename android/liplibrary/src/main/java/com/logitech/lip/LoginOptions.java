package com.logitech.lip;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Builder class to configure login options
 */
public final class LoginOptions implements Parcelable{

    private  boolean autoInstaller = false;
    private  boolean socialLogin = true;
    private  boolean isCreate;
    private  boolean isPersistToken = true;
    private  boolean isValidate = true;
    private  String  email;

    private boolean changeClaimsMode;

    private LoginOptions(Builder builder) {
        autoInstaller = builder.autoInstaller;
        socialLogin = builder.socialLogin;
        isCreate = builder.isCreate;
        isPersistToken = builder.isPersistToken;
        isValidate = builder.isValidate;
        email = builder.email;
        changeClaimsMode = builder.changeClaimsMode;
    }

    /**
     * Auto Installer Mode , In this case we should disable password filed
     * @return
     */
    public boolean isAutoInstaller() {
        return autoInstaller;
    }

    public boolean isSocialLogin() {
        return socialLogin;
    }

    /**
     * Returns if the flow is create user flow
     * @return true - if create user flow, false - if login flow
     */
    public boolean isCreate() {
        return isCreate;
    }

    /**
     * Returns if the tokens are to be persisted
     * @return true - if persisted, false - if not persisted
     */
    public boolean isPersistToken() {
        return isPersistToken;
    }

    /**
     * Returns the email that was passed from the client app
     * @return Email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns if validation is required for email/password
     * Incase of MH/MW validation is not required
     * @return true - if validation required, false - if validation not required
     */
    public boolean isValidate() {
        return isValidate;
    }

    /**
     * Change claims Mode is Used to fetch login details (Email, Password) or (Social tokens)
     * We should hit LIP in this scenario to validate the password.
     * Reason: because Social tokens can be used only once so we can't validate while change email or password
     * @return changeClaimsMode
     */
    public boolean isChangeClaimsMode() {
        return changeClaimsMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isAutoInstaller() ? 1 : 0);
        dest.writeInt(socialLogin? 1 : 0);
        dest.writeInt(isCreate? 1 : 0);
        dest.writeInt(isPersistToken? 1 : 0);
        dest.writeInt(isValidate? 1 : 0);
        dest.writeString(email);
        dest.writeInt(changeClaimsMode? 1 : 0);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {

        public LoginOptions createFromParcel(Parcel in) {
            return new LoginOptions.Builder()
                    .setAutoInstaller(in.readInt() == 1)
                    .setSocialLogin(in.readInt() == 1)
                    .setIsCreate(in.readInt() == 1)
                    .setIsPersistToken(in.readInt() == 1)
                    .setIsValidate(in.readInt() == 1)
                    .setEmail(in.readString())
                    .setChangeClaimsMode(in.readInt() == 1)
                    .build();
        }

        public LoginOptions[] newArray(int size) {

            return new LoginOptions[size];
        }
    };

    public static class Builder {
        private  boolean autoInstaller = false;
        private  boolean socialLogin = true;
        private  boolean isCreate;
        private  boolean isPersistToken = true;
        private  boolean isValidate = true;
        private  String  email;
        private boolean changeClaimsMode = false;

        /**
         * Set when non user is creating account
         * Password filed will be disabled and server will generate password and send mail
         * @param asInstaller
         */
        public Builder setAutoInstaller(boolean asInstaller) {
            this.autoInstaller = asInstaller;
            return this;
        }

        public Builder setSocialLogin(boolean socialLogin) {
            this.socialLogin = socialLogin;
            return this;
        }

        /**
         * Sets the user flow - create / login flow
         * @param isCreate
         * @return
         */
        public Builder setIsCreate(boolean isCreate) {
            this.isCreate = isCreate;
            return this;
        }

        /**
         * Sets the token persistence in LIP
         * @param isPersistToken
         * @return
         */
        public Builder setIsPersistToken(boolean isPersistToken) {
            this.isPersistToken = isPersistToken;
            return this;
        }

        /**
         * Sets the account validation
         * @param isValidate
         * @return
         */
        public Builder setIsValidate(boolean isValidate) {
            this.isValidate = isValidate;
            return this;
        }

        /**
         * Set the email
         * @param email
         * @return
         */
        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * Change claims Mode is Used to fetch login details (Email, Password) or (Social tokens)
         * We should hit LIP in this scenario to validate the password.
         * Reason: because Social tokens can be used only once so we can't validate while change email or password
         * @param changeClaimsMode
         */
        public Builder setChangeClaimsMode(boolean changeClaimsMode) {
            this.changeClaimsMode = changeClaimsMode;
            return this;
        }

        /**
         * Builds the LoginOptions
         * @return
         */
        public LoginOptions build() {
            return new LoginOptions(this);
        }
    }
}
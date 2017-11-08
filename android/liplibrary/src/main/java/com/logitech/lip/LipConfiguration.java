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
package com.logitech.lip;

/**
 * LipConfiguration enables the client apps to set the configuration of LIP SDK
 */
public final class LipConfiguration {

    private String serverUrl;
    private boolean verifyEmail;
    private IAnalytics analyticCallback;
    private ILogger loggerCallback;


    private String touUrl;
    private String ppUrl;

    /*
    * Active Identity to support multiple Identity
    * There is no Active Identity , we crate one upon login/create success.
    * In case Identity exist we update tokens
    */
    private String activeIdentity;

    /**
     * Returns URL of the LIP server
     * @return ServerURL
     */
    public String getServerUrl() {
        return serverUrl;
    }
    /**
     * Returns Terms of use url
     * @return terms of use url
     */
    public String getTermsUseUrl() {
        return touUrl;
    }
    /**
     * Returns PrivacyPolicy url
     * @return PrivacyPolicy url
     */
    public String getPrivacyPolicyUrl() {
        return ppUrl;
    }

    /**
     * Returns if email verification is required
     * @return true - if required, false - if not required
     */
    public boolean isVerifyEmail() {
        return verifyEmail;
    }

    public String getActiveIdentity() {
        return activeIdentity;
    }

    public IAnalytics getAnalyticCallback() {
        return analyticCallback;
    }

    public ILogger getLoggerCallback() {
        return loggerCallback;
    }

    public LipConfiguration( Builder builder){
        serverUrl = builder.serverUrl;
        verifyEmail = builder.isVerifyEmail;
        analyticCallback = builder.analyticCallback;
        loggerCallback = builder.loggerCallback;
        touUrl = builder.touUrl;
        ppUrl = builder.ppUrl;
        activeIdentity = builder.activeIdentity;

        if(builder.analyticCallback == null) {
            analyticCallback = Logger.getDefaultAnalytics();
        }

        if(builder.loggerCallback == null) {
            loggerCallback = Logger.getDefaultLogger();
        }
    }

    public static final class Builder {
        /*Base LIP Url */
        private String serverUrl;

        /* Email verification will be derived based on this flag*/
        private boolean isVerifyEmail;

        private IAnalytics analyticCallback;
        private ILogger loggerCallback;

        /*Terms of use Url*/
        private String touUrl;

        /*Privacy policy Url*/
        private String ppUrl;

        /*Active Identity to support multiple Identity
        * There is no Active Identity , we crate one upon login/create success.
        * In case Identity exist we update tokens
        */
        private String activeIdentity;

        /**
         * Sets URL of the LIP server
         * @param serverUrl ServerURL
         * @return Builder
         */
        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        /**
         * Sets if email verification is required
         * @param isVerifyEmail
         * @return Builder
         */
        public Builder setIsVerifyEmail(boolean isVerifyEmail) {
            this.isVerifyEmail = isVerifyEmail;
            return this;
        }

        /**
         * Sets the callback required to log analytics
         * @param analyticCallback
         * @return Builder
         */
        public Builder setAnalyticCallback(IAnalytics analyticCallback) {
            this.analyticCallback = analyticCallback;
            return this;
        }

        /**
         * Sets the callback required to log message
         * @param loggerCallback
         * @return Builder
         */
        public Builder setLoggerCallback(ILogger loggerCallback) {
            this.loggerCallback = loggerCallback;
            return this;
        }

        /**
         * @param touUrl Url to display Terms of use
         * @return Builder
         */
        public Builder setTermsUseUrl(String touUrl) {
            this.touUrl = touUrl;
            return this;
        }

        /**
         * @param privacyPolicyUrl Url to display privacy policy
         * @return
         */
        public Builder setPrivacyPolicyUrl(String privacyPolicyUrl) {
            this.ppUrl = privacyPolicyUrl;
            return this;
        }

        /*Active Identity to support multiple Identity
        * There is no Active Identity , we crate one upon login/create success.
        * In case Identity exist we update tokens
        */
        public Builder setActiveIdentity(String activeIdentity) {
            this.activeIdentity = activeIdentity;
            return this;
        }

        /**
         * Builds the LipConfiguration
         * @return LipConfiguration
         */
        public LipConfiguration build() {
            return new LipConfiguration(this);
        }
    }
}


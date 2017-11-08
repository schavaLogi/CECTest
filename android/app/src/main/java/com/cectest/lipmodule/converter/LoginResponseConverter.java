package com.cectest.lipmodule.converter;


import android.text.TextUtils;

import com.cectest.lipmodule.LoginResponse;
import com.logitech.lip.account.model.SocialIdentity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class LoginResponseConverter  extends JSObjectConverter<LoginResponse> {

    private interface Metadata {

        // User Info
        String USER_INFO = "userInfo";

        String EMAIL = "email";
        String PASSWORD = "password";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";

        // Add Social tokens
        String SOCIAL_IDENTITY = "social";
        String SOCIAL_PROVIDER = "provider";
        String SOCIAL_ACCESS_TOKEN = "accessToken";
        String SOCIAL_ID_TOKEN = "idToken";

        // Account Token
        String ACCOUNT_TOKEN = "accountToken";

        String ACCESS_TOKEN = "accessToken";
        String ID_TOKEN = "idToken";

        // Error codes and messages
        String ERROR_INFO = "errorInfo";

        String ERROR_CODE = "errorCode";
        String ERROR_MESSAGE = "errorMessage";
    }

    @Override
    public WritableMap toJSObject(LoginResponse response) {

        WritableMap result = Arguments.createMap();

        // Access Token upon success
        if(response.getAccountToken() != null) {
            WritableMap accountToken = Arguments.createMap();

            accountToken.putString(Metadata.ACCESS_TOKEN, response.getAccountToken().getAccessToken());
            accountToken.putString(Metadata.ID_TOKEN, response.getAccountToken().getIdToken());

            SocialIdentity social = response.getAccountToken().getSocial();

            if(social != null) {
                WritableMap socialIdentity = Arguments.createMap();
                socialIdentity.putString(Metadata.SOCIAL_ID_TOKEN, social.getIdToken());
                socialIdentity.putString(Metadata.SOCIAL_ACCESS_TOKEN, social.getAccessToken());
                socialIdentity.putString(Metadata.SOCIAL_PROVIDER, social.getProvider());

                accountToken.putMap(Metadata.SOCIAL_IDENTITY, socialIdentity);
            }
            result.putMap(Metadata.ACCOUNT_TOKEN, accountToken);
        }

        // UserInfo & Social Tokens
        if(response.getUserInfo() != null) {
            WritableMap userInfo = Arguments.createMap();

            userInfo.putString(Metadata.EMAIL, response.getUserInfo().getEmail());
            userInfo.putString(Metadata.PASSWORD, response.getUserInfo().getPassword());
            userInfo.putString(Metadata.FIRST_NAME, response.getUserInfo().getFirstName());
            userInfo.putString(Metadata.LAST_NAME, response.getUserInfo().getLastName());
            SocialIdentity social = response.getUserInfo().getSocial();
            if( social != null) {
                WritableMap socialIdentity = Arguments.createMap();
                socialIdentity.putString(Metadata.SOCIAL_ID_TOKEN, social.getIdToken());
                socialIdentity.putString(Metadata.SOCIAL_ACCESS_TOKEN, social.getAccessToken());
                socialIdentity.putString(Metadata.SOCIAL_PROVIDER, social.getProvider());
                userInfo.putMap(Metadata.SOCIAL_IDENTITY, socialIdentity);
            }
            result.putMap(Metadata.USER_INFO, userInfo);
        }

        if(!TextUtils.isEmpty(response.getErrorCode()) && !TextUtils.isEmpty(response.getErrorMessage())) {
            WritableMap errorInfo = Arguments.createMap();

            errorInfo.putString(Metadata.ERROR_CODE, response.getErrorCode());
            errorInfo.putString(Metadata.ERROR_MESSAGE, response.getErrorMessage());

            result.putMap(Metadata.ERROR_INFO, errorInfo);
        }
        return result;
    }
}


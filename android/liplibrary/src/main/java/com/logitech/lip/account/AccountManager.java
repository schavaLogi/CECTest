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

package com.logitech.lip.account;


import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.logitech.lip.ILogger;
import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.R;
import com.logitech.lip.SecurePrefManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.ChangeClaims;
import com.logitech.lip.account.model.ChangePassword;
import com.logitech.lip.account.model.SignInResponse;
import com.logitech.lip.account.model.SignOut;
import com.logitech.lip.account.model.SocialIdentity;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.IListener;
import com.logitech.lip.network.LIPRequest;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.utility.RandomGenerator;
import com.logitech.lip.volley.DefaultRetryPolicy;
import com.logitech.lip.volley.RetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * The utility class that provides the LIP API's.
 *
 * This class acts as a point of contact for the clients who uses LIP API without UI
 */
public final class AccountManager {

    private static final String TAG = AccountManager.class.getSimpleName();

    /* Logitech identiy provider client id*/
    private static final String CLIENT_ID; //"ee10d1b6-d851-464c-8c80-6666522cde1d";

    private static final String IDENTITY = "identity";
    private static final String SIGN_IN = "signin";
    private static final String REFRESH = "refresh";
    private static final String SIGN_OUT = "signout";
    private static final String USER_INFO = "userInfo";
    private static final String VERIFY = "verify";
    private static final String CHANGE = "change";
    private static final String FORGOT = "forgot";
    private static final String RESEND = "resend";
    private static final String CHANGE_CLAIMS = "changeclaims";

    private static String BASE_URL;


    /*No need of retry certain request , that might cause issue like create / refresh */
    private static RetryPolicy REQUEST_NO_RETRY = new DefaultRetryPolicy(20000,-1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    static {
        LIPSdk.isInitialized();

        CLIENT_ID = LIPSdk.getContext().getString(R.string.logitech_app_id);
        BASE_URL = LIPSdk.getConfiguration().getServerUrl();

        if(TextUtils.isEmpty(CLIENT_ID) || TextUtils.isEmpty(BASE_URL)) {
            throw new IllegalArgumentException("Logitech client and server url must not be null");
        }
    }


    private AccountManager() {
    }

    /**
     * This API must be called when there is a change in LipSdk Configuration
     * TODO : Refactor as this api is internal to SDK should not expose to clients
     */
    public static void updateActiveIdentity() {
        BASE_URL = LIPSdk.getConfiguration().getServerUrl();
        if(TextUtils.isEmpty(CLIENT_ID) || TextUtils.isEmpty(BASE_URL)) {
            throw new IllegalArgumentException("Logitech client and server url must not be null");
        }
        AccountTokenManager.getInstance().updateActiveIdentity();
    }

    /**
     * Used to create an account in LIP Service.
     * SignIn or create will depend on create attribute in userInfo
     *
     * @param userInfo The input provided by the user to create an account
     * @param listener The callback that should be called on the result of the API
     * @see UserInfo
     */
    public static void create(UserInfo userInfo, ResponseListener<AccountToken> listener) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(SIGN_IN);
        String uri = builder.toString();

        userInfo.setLanguage(getCurrentLocale());

        // Build Param
        String body = getCreateParamsBody(userInfo);
        validateUserInfo(userInfo);
        SignInResponseListener resListener = new SignInResponseListener(userInfo, listener);

        LIPRequest<SignInResponse> request = new LIPRequest<>(uri,
                SignInResponse.class,
                LIPRequest.Method.POST,
                getHeaders(), body,
                resListener);

        Logger.debug(TAG, "Create", "URI : " + uri + " Data : " + removeCriticalInfo(body));
        request.setRetryPolicy(REQUEST_NO_RETRY);
        request.execute();
    }

    public static void create(UserInfo userInfo, Map<String, Object> claims,
                              ResponseListener<AccountToken> listener) {

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(SIGN_IN);
        String uri = builder.toString();

        userInfo.setLanguage(getCurrentLocale());

        // Build Param
        String body = getCreateParamsBody(userInfo);

        validateUserInfo(userInfo);

        String claimsBody = new Gson().toJson(claims);
        body = mergeJSONStrings(body, claimsBody);

        SignInResponseListener resListener = new SignInResponseListener(userInfo, listener);

        LIPRequest<SignInResponse> request = new LIPRequest<>(uri,
                SignInResponse.class,
                LIPRequest.Method.POST,
                getHeaders(), body,
                resListener);
        request.setRetryPolicy(REQUEST_NO_RETRY);

        Logger.debug(TAG, "Create", "URI : " + uri + " Data : " + removeCriticalInfo(body));
        request.execute();
    }

    /**
     * Used to signIn account in LIP Service.
     * SignIn or create will depend on create attribute in userInfo
     *
     * @param userInfo The input provided by the user to login
     * @param listener The callback that should be called on the result of the API
     * @see UserInfo
     */
    public static void signIn(UserInfo userInfo, ResponseListener<AccountToken> listener) {

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(SIGN_IN);
        String uri = builder.toString();

        boolean isCreate = userInfo.isCreate();
        if(isCreate) {
            userInfo.setLanguage(getCurrentLocale());
            validateUserInfo(userInfo);
        }
        boolean isTokenPersist = userInfo.isPersist();

        // Build Param
        String body = getSignInParamsBody(userInfo);

        SignInResponseListener resListener = new SignInResponseListener(userInfo, listener);

        LIPRequest<SignInResponse> request = new LIPRequest<>(uri,
                SignInResponse.class,
                LIPRequest.Method.POST,
                getHeaders(), body,
                resListener);

        /*  No need of retry request either network slow or server not reachable
         *  Server consider as multiple request and block account in case of wrong password
         */
        request.setRetryPolicy(REQUEST_NO_RETRY);
        Logger.debug(TAG, "signIn", "URI : " + uri + " Data : " + removeCriticalInfo(body));
        request.execute();
    }

    /**
     * Used to signIn with Social identity (i.e facebook, google , ..)
     *
     * @param identity social identity tokens
     * @param listener The callback that should be called on the result of the API
     * @see SocialIdentity
     */
    public static void signIn(SocialIdentity identity, ResponseListener<AccountToken> listener) {

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(SIGN_IN);
        String uri = builder.toString();

        String body = getSignInParamsBody(identity, null, null);

        UserInfo userInfo = new UserInfo(identity);
        userInfo.setIsPersist(true);

        SignInResponseListener resListener = new SignInResponseListener(userInfo, listener);

        LIPRequest<SignInResponse> request = new LIPRequest<>(uri,
                SignInResponse.class,
                LIPRequest.Method.POST,
                getHeaders(), body,
                resListener);
        request.setRetryPolicy(REQUEST_NO_RETRY);
        Logger.debug(TAG, "signIn", "URI : " + uri + " Data : " + removeCriticalInfo(body));
        request.execute();
    }

    /**
     * Used to signIn with Logi email and password
     *
     * @param email    email to sign-in
     * @param password password to sign-in
     * @param listener The callback that should be called on the result of the API
     */
    public static void signIn(String email, String password,
                              ResponseListener<AccountToken> listener) {

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(SIGN_IN);
        String uri = builder.toString();

        String body = getSignInParamsBody(null, email, password);

        UserInfo userInfo = new UserInfo(email, password, false);
        userInfo.setIsPersist(true);

        SignInResponseListener resListener = new SignInResponseListener(userInfo, listener);

        //Generate Me
        LIPRequest<SignInResponse> request = new LIPRequest<>(uri,
                SignInResponse.class,
                LIPRequest.Method.POST, getHeaders(),
                body,
                resListener);
        request.setRetryPolicy(REQUEST_NO_RETRY);
        Logger.debug(TAG, "signIn", "URI : " + uri + " Data : " + removeCriticalInfo(body));
        request.execute();
    }

    /**
     * Used to signout , capable of clearing all sessions
     *
     * @param allSessions to clear all sessions or only current session
     * @param accessToken lip access token
     * @param listener The callback that should be called on the result of the API
     */
    public static void signOut(boolean allSessions, String accessToken,
                               ResponseListener<Object> listener) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(SIGN_OUT);
        String uri = builder.toString();

        Map<String, String> headers = getHeaders();
        headers.put("Authorization", "Bearer " + accessToken);

        String paramsBody = getSignOutParamsBody(null, allSessions);

        LIPRequest<Object> request = new LIPRequest<>(uri,
                Object.class,
                LIPRequest.Method.POST, headers, paramsBody,
                listener);

        Logger.debug(TAG, "signOut", "URI : " + uri + " Data : " + paramsBody);
        request.execute();
    }


    /**
     * Used to refresh Account token when token expired
     * <p> In certain scenario the account token could not be refreshed. In such a case the user is forced to the login screen again </p>
     * @param listener The callback that should be called on the result of the API
     */
    public static void refreshAccountToken(/*String refreshToken,*/ ResponseListener<AccountToken> listener) {
        SecurePrefManager securePref = new SecurePrefManager(LIPSdk.getContext());
        final String activeIdentity = AccountTokenManager.getInstance().getActiveIdentity();
        String refreshToken = securePref.getData(AccountTokenManager.REFRESH_TOKEN_KEY + activeIdentity, null, true);
        String socialToken = securePref.getData(AccountTokenManager.SOCIAL_IDENTITY_TOKEN + activeIdentity, null, true);

        Logger.debug(TAG, "refreshAccountToken", "activeIdentity = " + activeIdentity);

        if(refreshToken != null) {
            Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
            builder.appendPath(IDENTITY).appendPath(REFRESH);
            String uri = builder.toString();

            String paramsBody = getRefreshParamsBody(refreshToken/*, null*/);
            SocialIdentity socialIdentity = null;
            if(socialToken != null) {
                try {
                    socialIdentity = new Gson().fromJson(socialToken, SocialIdentity.class);
                } catch (JsonSyntaxException e) {

                }
            }
            UserInfo userInfo = new UserInfo(socialIdentity);
            userInfo.setEmail(activeIdentity);
            userInfo.setIsPersist(true);

            SignInResponseListener resListener = new SignInResponseListener(userInfo, listener);

            LIPRequest<SignInResponse> request = new LIPRequest<>(uri,
                    SignInResponse.class,
                    LIPRequest.Method.POST, getHeaders(), paramsBody,
                    resListener);

            Logger.debug(TAG, "refreshAccountToken", "URI : " + uri + " Data : " + paramsBody);

            request.setRetryPolicy(REQUEST_NO_RETRY);

            request.execute();
        }else{
            if (listener != null) {
                listener.onError(IListener.ErrorCode.ERROR_NULL_REFRESH_TOKEN, "Login required");
            }
            Logger.debug(TAG, "refreshAccountToken", "refreshToken is null. Login required");
        }
    }

    /**
     * Used to change the LIP password
     * @param accessToken Access token of the account
     * @param changePassword holds data to change password
     * @param listener The callback that should be called on the result of the API
     * @see ChangePassword
     */
    public static void changePassword(String accessToken,ChangePassword changePassword, ResponseListener<Object> listener) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(CHANGE);
        String uri = builder.toString();

        Map<String, String> headers = getHeaders();
        headers.put("Authorization", "Bearer " + accessToken);

        ChangePasswordListener resListener = new ChangePasswordListener(changePassword, listener);

        String paramsBody = getChangeParamsBody(changePassword);
        LIPRequest<Object> request = new LIPRequest<>(uri, Object.class,
                LIPRequest.Method.POST, headers, paramsBody,
                resListener);

        Logger.debug(TAG, "changePassword", "URI : " + uri + " Data : " + removeCriticalInfo(paramsBody));
        request.execute();
    }

    /**
     * Request to get Password reset mail to the specified email account when user forgot password
     * @param email email
     * @param listener The callback that should be called on the result of the API
     */
    public static void forgotPassword(String email, ResponseListener<Object> listener) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(FORGOT);
        String uri = builder.toString();

        String paramsBody = getForgotParamsBody(email);
        LIPRequest<Object> request = new LIPRequest<>(uri, Object.class,
                LIPRequest.Method.POST, getHeaders(), paramsBody,
                listener);

        Logger.debug(TAG, "forgotPassword", "URI : " + uri + " Data : " + paramsBody);
        request.execute();
    }

    /**
     * Will fetch user Profile information including profile picture when login through Social Identity
     * Same can be used to check the User email is verified or not
     *
     * @param accessToken accessToken
     * @param listener  The callback that should be called on the result of the API
     */
    public static void getUserInfo(String accessToken, ResponseListener<UserInfo> listener) {

        Map<String, String> headers = getHeaders();
        headers.put("Authorization", "Bearer " + accessToken);

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(USER_INFO);
        builder.appendQueryParameter("client_id", CLIENT_ID);
        String uri = builder.toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        String paramsBody = new Gson().toJson(params);

        LIPRequest<UserInfo> request = new LIPRequest<>(uri,
                UserInfo.class,
                LIPRequest.Method.GET, headers, paramsBody,
                listener);

        Logger.debug(TAG, "getUserInfo", "URI : " + uri + " Data : " + paramsBody);
        request.execute();
    }

    /**
     * Will fetch user Profile information including profile picture when login through Social Identity
     * Same can be used to check the User email is verified or not
     *
     * We can fetch client specific claims in raw format
     *
     * @param accessToken accessToken
     * @param listener  The callback that should be called on the result of the API
     */
    public static void getClaims(String accessToken, ResponseListener<String> listener) {

        Map<String, String> headers = getHeaders();
        headers.put("Authorization", "Bearer " + accessToken);

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(USER_INFO);
        builder.appendQueryParameter("client_id", CLIENT_ID);
        String uri = builder.toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        String paramsBody = new Gson().toJson(params);

        LIPRequest<String> request = new LIPRequest<>(uri,
                String.class,
                LIPRequest.Method.GET, headers, paramsBody,
                listener);

        Logger.debug(TAG, "getUserInfo", "URI : " + uri + " Data : " + paramsBody);
        request.execute();
    }

    /**
     * Will be used to Send Verification mail to the account specific email to validate account
     *
     * @param accessToken Access token
     * @param listener The callback that should be called on the result of the API
     */
    public static void resendVerificationMail(String accessToken, ResponseListener<Object> listener) {
        Map<String, String> headers = getHeaders();
        headers.put("Authorization", "Bearer " + accessToken);

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(RESEND);
        // builder.appendQueryParameter("client_id", CLIENT_ID);
        String uri = builder.toString();


        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        String paramsBody = new Gson().toJson(params);


        LIPRequest<Object> request = new LIPRequest<>(uri,
                Object.class,
                LIPRequest.Method.POST, headers, paramsBody,
                listener);

        Logger.debug(TAG, "resendVerificationMail", "URI : " + uri );
        request.execute();
    }

    /**
     * Changes the claims of the account. The claim can be
     * <ul>
     *     <li>Email</li>
     *     <li>First name</li>
     *     <li>Last name</li>
     *     <li>Country</li>
     * </ul>
     * <p> If claims has email then other data will be ignored
     *     To change claims other than email need to ignore the email field
     *     Email & claims can't be changed same time because the new mail need to verified</p>
     *
     * @param accessToken Access token
     * @param changeClaims changeClaims
     * @param listener  The callback that should be called on the result of the API
     * @see ChangeClaims
     */
    public static void changeClaims(String accessToken , ChangeClaims changeClaims,
                                    ResponseListener<Object> listener) {
        Map<String, String> headers = getHeaders();
        headers.put("Authorization", "Bearer " + accessToken);

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(CHANGE_CLAIMS);
        String uri = builder.toString();

        String paramsBody = getChangeClaims(changeClaims);
        LIPRequest<Object> request = new LIPRequest<>(uri, Object.class,
                LIPRequest.Method.POST, headers, paramsBody,
                listener);

        Logger.debug(TAG, "changeClaims", "URI : " + uri + " Data : " + paramsBody);
        request.execute();
    }

    /**
     * Generic claims to support other than email , profile related
     * @param accessToken current Account accessToken
     * @param appClaims   claims specific to current application
     * @param listener  callback listener
     */
    public static void changeClaims(String accessToken ,
                                    Map<String, Object> appClaims,
                                    ResponseListener<Object> listener) {
        Map<String, String> headers = getHeaders();
        headers.put("Authorization", "Bearer " + accessToken);

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        builder.appendPath(IDENTITY).appendPath(CHANGE_CLAIMS);
        String uri = builder.toString();

        // Build Param
        appClaims.put("client_id", CLIENT_ID);
        String paramsBody = new Gson().toJson(appClaims);

        LIPRequest<Object> request = new LIPRequest<>(uri, Object.class,
                LIPRequest.Method.POST, headers, paramsBody,
                listener);

        Logger.debug(TAG, "changeClaims", "URI : " + uri + " Data : " + paramsBody);
        request.execute();
    }


    // Private functions to get POST params
    private static String getCreateParamsBody(UserInfo userInfo) {
        userInfo.setChannelId(new RandomGenerator().getRandomString());
        userInfo.setClientId(CLIENT_ID);
        userInfo.setCreate(true);
        // userInfo.setVerifyEmail(true);

        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        return builder.create().toJson(userInfo);
    }

    private static String getSignInParamsBody(UserInfo userInfo) {

        userInfo.setChannelId(new RandomGenerator().getRandomString());
        userInfo.setClientId(CLIENT_ID);
        final GsonBuilder builder = new GsonBuilder();

        builder.excludeFieldsWithoutExposeAnnotation();
        return builder.create().toJson(userInfo);
    }

    private static String getSignInParamsBody(SocialIdentity identity,
                                              String email, String password) {
        UserInfo userInfo = new UserInfo(identity);
        userInfo.setEmail(email);
        userInfo.setPassword(password);
        userInfo.setChannelId(new RandomGenerator().getRandomString());
        userInfo.setClientId(CLIENT_ID);

        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        return builder.create().toJson(userInfo);
    }

    private static String getRefreshParamsBody(String refreshToken/*, String hMac*/) {
        //Build Param
        HashMap<String, String> params = new HashMap<>();

        params.put("refresh_token", refreshToken);
        params.put("client_id", CLIENT_ID);

        return new Gson().toJson(params);
    }


    private static String getSignOutParamsBody(String accessToken, boolean allSessions) {

        SignOut signOut = new SignOut(accessToken, allSessions);
        signOut.setClientId(CLIENT_ID);
        return new Gson().toJson(signOut);
    }

    private static String getChangeParamsBody(ChangePassword changePassword) {
        changePassword.setClientId(CLIENT_ID);
        return new Gson().toJson(changePassword);
    }

    private static String getForgotParamsBody(String email) {
        // Build Param
        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("email", email);

        return new Gson().toJson(params);
    }

    private static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return headers;
    }

    private static String getChangeClaims(ChangeClaims changeClaims) {
        changeClaims.setClientId(CLIENT_ID);
        return new Gson().toJson(changeClaims);
    }

    private static String mergeJSONStrings(String jsonString1, String jsonString2) {
        JSONObject mergedJson = new JSONObject();
        try {
            if (jsonString1.length() > 0) {
                mergedJson = new JSONObject(jsonString1);
            }
            if (jsonString2.length() > 0) {
                JSONObject object = new JSONObject(jsonString2);
                for(Iterator<String> iter = object.keys();iter.hasNext();) {
                    String key = iter.next();
                    mergedJson.put(key, object.get(key));
                }
            }
        } catch (Exception e) {
            Logger.error(ILogger.LIP_E001, TAG, "mergeJSONStrings",
                    "Exception json1=" + removeCriticalInfo(jsonString1) +
                            "json2=" + removeCriticalInfo(jsonString2), e);
        }
        return mergedJson.toString();
    }

    /**
     * Used to set current LIP account token (Refresh token, Access token , id token)
     *
     * @param response lip sign-in response
     * @see SignInResponse
     */
    public static void setCurrentAccountToken(SignInResponse response, UserInfo userInfo) {
        AccountTokenManager.getInstance().setCurrentAccountToken(response, userInfo);
    }

    /**
     * Used to get Account token which is cached upon login
     * In case of Token expire it will fetch new account token and update through callback
     * {@link AccountTokenTracker} for continuous token tracking
     *
     * @param listener callback listener
     */
    public static void getCurrentAccountToken(ResponseListener<AccountToken> listener) {
        AccountTokenManager.getInstance().getCurrentAccountToken(false, listener);
    }

    /**
     * Used to get Account token which is cached upon login
     * In case of Token expire it will fetch new account token and update through callback
     * {@link AccountTokenTracker} for continuous token tracking
     *
     * @param listener callback listener
     */
    public static void getCurrentAccountToken(boolean autoRefresh, ResponseListener<AccountToken> listener) {
        AccountTokenManager.getInstance().getCurrentAccountToken(autoRefresh, listener);
    }

    private static void validateUserInfo(UserInfo userInfo) {
        if(userInfo == null) {
            throw new IllegalArgumentException("Not a valid input");
        } else if(userInfo.getSocial() != null) {
            // Check it is social login or not, Validate tokens present or not
            if(TextUtils.isEmpty(userInfo.getSocial().getProvider()) ||
                    TextUtils.isEmpty(userInfo.getSocial().getAccessToken()) ||
                    TextUtils.isEmpty(userInfo.getSocial().getIdToken())) {
                throw new IllegalArgumentException("In case of social, token and provider must not be null");
            }
        } else {
            String email = userInfo.getEmail();
            if(TextUtils.isEmpty(email)) {
                throw new IllegalArgumentException("Email must present");
            }

            if (!email.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                throw new IllegalArgumentException("Email is not a valid pattern");
            }

            /*  No need to check password because server side will generate in case it missed

            String password = userInfo.getPassword();
            if (TextUtils.isEmpty(password) || (userInfo.isCreate() && password.length() < 8)) {
                throw new IllegalArgumentException("Password not met required criteria");
            }

            */

            if(userInfo.isCreate() && ( TextUtils.isEmpty(userInfo.getFirstName()) ||
                    TextUtils.isEmpty(userInfo.getLastName()) )) {
                throw new IllegalArgumentException("First name or last name is missing");
            }
        }
    }

    private static String getCurrentLocale() {
        Locale locale = LIPSdk.getContext().getResources().getConfiguration().locale;
        return locale.getLanguage() + "-" + locale.getCountry();
    }

    private static String removeCriticalInfo(String jsonString) {
        try {
            JSONObject bodyObj;
            bodyObj = new JSONObject(jsonString);
            bodyObj.remove("password");
            bodyObj.remove("new_password");
            bodyObj.remove("old_password");
            return bodyObj.toString();
        } catch (JSONException e) {
            Logger.debug(TAG, "removeCriticalInfo", "Exception json=" + jsonString + e.getMessage());
        }
        return jsonString;
    }

    /**
     * Internal SignIn listener to store tokens based on persist flag
     */
    private static class SignInResponseListener extends ResponseListener<SignInResponse> {

        private final ResponseListener<AccountToken> listener;
        private final boolean isTokenPersist;
        private final UserInfo userInfo;

        SignInResponseListener(UserInfo userInfo , ResponseListener<AccountToken> listener) {
            this.userInfo = userInfo;
            this.listener = listener;
            this.isTokenPersist = userInfo.isPersist();
        }

        @Override
        public void onSuccess(SignInResponse result) {
            Logger.info(AccountManager.TAG,"SignInResponseListener","onSuccess");

            if (isTokenPersist) {
                setCurrentAccountToken(result, userInfo);
            }

            if (listener != null) {
                AccountToken accToken = result.getAccountToken();

                if(userInfo != null && userInfo.getSocial() != null) {
                    accToken.setSocial(userInfo.getSocial());
                }

                listener.onSuccess(accToken);
            }
        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            Logger.debug(AccountManager.TAG, "SignInResponseListener",
                    "onError errorCode =" + errorCode + "errorMsg =" + errorMessage);
            if (listener != null) {
                listener.onError(errorCode, errorMessage);
            }
        }
    }

    /**
     * Internal class to do silent login when client changes password and it's success
     */
    private static class ChangePasswordListener extends ResponseListener<Object> {
        private final ChangePassword changePassword;
        private final ResponseListener<Object> listener;

        public ChangePasswordListener(ChangePassword changePassword,
                                      ResponseListener<Object> listener) {
            this.changePassword = changePassword;
            this.listener = listener;
        }

        @Override
        public void onSuccess(Object result) {
            Logger.info(AccountManager.TAG,"ChangePasswordListener","onSuccess");
            setCurrentAccountToken(null, null);
            // Do silent login
            AccountManager.signIn(changePassword.getEmail(),
                    changePassword.getNewPassword(), null);
            Logger.info(AccountManager.TAG, "ChangePasswordListener", "silent login");
            if(listener != null) {
                listener.onSuccess(result);
            }
        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            Logger.debug(AccountManager.TAG,"ChangePasswordListener","onError");
            if(listener != null) {
                listener.onError(errorCode,errorMessage);
            }
        }
    }
}

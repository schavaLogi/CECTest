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

package com.logitech.lip.ui.social;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.logitech.lip.ILogger;
import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.R;
import com.logitech.lip.account.model.SocialIdentity;
import com.logitech.lip.profile.SocialProfile;
import com.logitech.lip.ui.SocialClient;
import com.logitech.lip.ui.common.ProgressDialog;
import com.logitech.lip.ui.social.listeners.SocialClientListener;
import com.logitech.lip.ui.social.listeners.SocialClientLoginListener;
import com.logitech.lip.utility.RandomGenerator;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class FacebookClient extends SocialClient implements FacebookCallback<LoginResult>{

    private static final String TAG = FacebookClient.class.getSimpleName();

    private final List<String> permission = new ArrayList<>();
    private CallbackManager callbackManager;
    private ProgressDialog dialog;
    private SocialIdentity identity;
    private SocialProfile profile;

    public FacebookClient() {
        initialize();
    }

    @Override
    public void initialize() {
        Logger.info(TAG, "initialize","Initializing facebook client");
        FacebookSdk.sdkInitialize(LIPSdk.getContext());
        permission.add("email");
        permission.add("public_profile");

        identity = new SocialIdentity(Provider.PROVIDER_FACEBOOK, null);
        identity.setIdToken(new RandomGenerator().getRandomString());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.debug(TAG, "onActivityResult", "requestCode =" + requestCode + "resultCode=" + resultCode);

        if(callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);

            Logger.debug(TAG, "onActivityResult", "requestCode = " + requestCode +
                    "resultCode =" + resultCode);
        }
    }

    public void requestLogin(Activity activity, SocialClientLoginListener listener){
        super.requestLogin(activity, listener);

        Logger.info(TAG, "requestLogin", "Requesting login in facebook");

        //if( token == null) {
        LoginManager.getInstance().logInWithReadPermissions(activity,permission);
    }

    public  void requestLogout(){
        Logger.info(TAG, "requestLogout","Requesting logout in facebook");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null,
                HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                Logger.info(TAG, "requestLogout","Logout request completed in facebook");
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    public SocialProfile getProfile(){
        return profile;
    }

    private void requestSocialProfile(AccessToken accessToken) {
        Logger.info(TAG,"requestSocialProfile", "Reading profile info..");
        showProgressDialog(getActivity().getString(R.string.lip_sign_up_facebook_reading_info));

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if(object != null) {
                                profile = new SocialProfile(object.optString("id"),
                                        object.optString("name"));
                                profile.firstName = object.optString("first_name");
                                profile.lastName = object.optString("last_name");
                                profile.email = object.optString("email");
                                profile.profileURL = object.getString("link");
                                profile.imgUrl = new URL("https://graph.facebook.com/" +
                                        profile.id + "/picture?type=large").toString();
                            }
                        } catch (Exception e ){
                            Logger.error(ILogger.LIP_E002,TAG,
                                    "requestSocialProfile","requestSocialProfile msg =" + e.getMessage(), e);
                        }

                        dismissProgressDialog();

                        if(profile == null || TextUtils.isEmpty(profile.email)) {
                            localListeners.get(REQUEST_LOGIN).onSocialLoginError(SocialClientListener.SERVICE_MISSING_PERMISSION,
                                    getActivity().getString(R.string.lip_sign_up_error_toast_no_email_facebook));
                            Logger.debug(TAG, "requestSocialProfile", "Completed reading profile info.." + " No email id associated with the account");
                        } else {
                            ((SocialClientLoginListener)
                                    localListeners.get(REQUEST_LOGIN)).onSocialLoginSuccess(identity);
                            Logger.debug(TAG, "requestSocialProfile", "Completed reading profile info.." + "Email : " + profile.getEmail());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, first_name, last_name, email, link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void showProgressDialog(String title) {
        if(dialog != null && dialog.isShowing()) {
            dialog.setProgressTitle(title);
            //dialog.show();
        } else {
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.setProgressTitle(title);
            dialog.show();
        }
    }

    private void dismissProgressDialog() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    @Override
    public void onSuccess(LoginResult loginResult) {
        if(loginResult != null && loginResult.getAccessToken() != null) {
            Set<String> deniedPermissions = loginResult.getRecentlyDeniedPermissions();
            if (deniedPermissions != null && deniedPermissions.size() > 0 &&
                    deniedPermissions.contains("email")) {
                dismissProgressDialog();
                localListeners.get(REQUEST_LOGIN).onSocialLoginError(SocialClientListener.SERVICE_MISSING_PERMISSION,
                        getActivity().getString(R.string.lip_sign_up_error_toast_no_email_access_facebook));
                Logger.debug(TAG, "onSuccess", "Email access disabled in facebook");
            } else {
                identity.setAccessToken(loginResult.getAccessToken().getToken());
                requestSocialProfile(loginResult.getAccessToken());
                Logger.info(TAG,"onSuccess","Authorized by facebook and fetched access token" );
            }
        } else {
            dismissProgressDialog();
            localListeners.get(REQUEST_LOGIN).onSocialLoginError(SocialClientListener.INTERNAL_ERROR,
                    getActivity().getString(R.string.lip_sign_up_error_toast_internal_error));
            if(loginResult != null && loginResult.toString() != null) {
                Logger.error(ILogger.LIP_E002, TAG, "onSuccess",
                        "Authorization by facebook failed " + loginResult.toString(), new Exception());
            }
        }

    }

    @Override
    public void onCancel() {
        Logger.debug(TAG, "onCancel", "User cancelled login");
        dismissProgressDialog();
       /* localListeners.get(REQUEST_LOGIN).onSocialLoginError(SocialClientListener.CANCELED,
                getActivity().getString(R.string.lip_sign_up_error_toast_cancel_error));*/
    }

    @Override
    public void onError(FacebookException error) {
        /*
         * Check if the previous and current user are different but previous token exists do logout and login
         */
        if (error instanceof FacebookAuthorizationException &&
                (error.getMessage().contains("User logged in as different Facebook user") ||
                        error.getMessage().contains("Error validating access token")) &&
                AccessToken.getCurrentAccessToken() != null) {
            Logger.debug(TAG, "onError", "Login Failed reason=" + error.getMessage());
            LoginManager.getInstance().logOut();
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), permission);

        } else {
            Logger.error(ILogger.LIP_E002, TAG, "onError",
                    "Login Failed reason=" + error.getMessage(), error);
            dismissProgressDialog();
            localListeners.get(REQUEST_LOGIN).onSocialLoginError(SocialClientListener.INTERNAL_ERROR,
                    getErrorMessage(error));
        }
    }

    private String getErrorMessage(FacebookException error) {
        if(error != null && error.getMessage() != null) {
            if(error.getMessage().contains("net::") ||
                    error.getMessage().contains("CONNECTION_FAILURE:")){
                Logger.debug(TAG, "onError", "Login Failed reason=" + error.getMessage());
                return getActivity().getString(R.string.lip_sign_up_error_toast_no_network);
            } else if(error.getMessage().contains("Invalid key hash. The key hash")){
                Logger.error(ILogger.LIP_E002, TAG, "onError",
                        "Login Failed reason=" + error.getMessage(), error);
                return getActivity().getString(R.string.lip_sign_up_error_toast_internal_error);
            }
        }
        return getActivity().getString(R.string.lip_sign_up_error_toast_timeout);
    }
}

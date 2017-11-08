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

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import java.io.IOException;

final class GoogleClient extends SocialClient implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleClient.class.getSimpleName();

    private static final int RC_SIGN_IN = 1000;

    private static String SERVER_CLIENT_ID;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount account;

    private SocialIdentity identity;
    private SocialProfile profile;

    private ProgressDialog dialog;

    GoogleClient() {
        SERVER_CLIENT_ID = LIPSdk.getContext().getString(R.string.server_client_id);

        if(TextUtils.isEmpty(SERVER_CLIENT_ID)) {
            throw new IllegalArgumentException("Google server client id must not be null");
        }

        initialize();
    }

    @Override
    public void initialize() {
        Logger.info(TAG, "initialize", "Initializing google client");
        googleApiClient = buildGoogleApiClient(LIPSdk.getContext());
        identity = new SocialIdentity(SocialClient.Provider.PROVIDER_GOOGLE, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private GoogleApiClient buildGoogleApiClient(Context context) {
        GoogleSignInOptions.Builder gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail();

        if (!TextUtils.isEmpty(SERVER_CLIENT_ID)) {
            gsoBuilder.requestIdToken(SERVER_CLIENT_ID);
            gsoBuilder.requestServerAuthCode(SERVER_CLIENT_ID);
        }

        return new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gsoBuilder.build())
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        hideProgressDialog();
        /*
        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().showErrorDialogFragment(getActivity(), connectionResult.getErrorCode(), 0);
        }
        */
        Logger.error(ILogger.LIP_E002, TAG, "onConnectionFailed",
                "Connection to google client failed", new Exception(connectionResult.getErrorMessage()));

        notifyLoginStatus(false, SocialClientListener.INTERNAL_ERROR,
                getActivity().getString(R.string.lip_sign_up_error_toast_internal_error));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            Logger.debug(TAG, "onActivityResult", "resultCode:"+resultCode);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            hideProgressDialog();
            Logger.debug(TAG, "onActivityResult",
                    "Request got cancelled requestCode =" + requestCode + "resultCode=" +resultCode);

            /*notifyLoginStatus(false, SocialClientListener.INTERNAL_ERROR,
                    getActivity().getString(R.string.lip_sign_up_error_toast_internal_error));*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void requestLogin(Activity activity, SocialClientLoginListener listener) {
        super.requestLogin(activity, listener);

        Logger.info(TAG, "requestLogin", "Requesting login in Google");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int playServeAvailable = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if(playServeAvailable == ConnectionResult.SUCCESS) {
            googleApiClient.connect();

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);

            showProgressDialog();
        } else if (apiAvailability.isUserResolvableError(playServeAvailable) &&
                apiAvailability.showErrorDialogFragment(getActivity(), playServeAvailable, 0)) {
            Logger.info(TAG, "requestLogin", "Google play service not available yet!!!");
        }
    }

    @Override
    public void requestLogout() {
        Logger.info(TAG, "requestLogout", "Requesting logout in Google");
        if(googleApiClient != null && googleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                        }
                    });
        }
        account = null;
    }

    @Override
    public SocialProfile getProfile() {
        return profile;
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Logger.debug(TAG, "handleSignInResult:" ,"Result : "+ result.isSuccess());
        if(result.isSuccess() && result.getSignInAccount() != null) {
            account = result.getSignInAccount();

            fetchProfileAndAccessToken(false, account);
        } else {
            notifyLoginStatus(false, SocialClientListener.INTERNAL_ERROR,
                    getActivity().getString(R.string.lip_sign_up_error_toast_internal_error));
            Logger.error(ILogger.LIP_E002, TAG, "handleSignInResult",
                    "Result Failure or account is null", new Exception());
        }
    }

    private void setProfileInfo(GoogleSignInAccount  account){
        if(account != null) {
            profile = new SocialProfile(account.getId(), account.getDisplayName());
            profile.email = account.getEmail();
            profile.name = account.getDisplayName();
            if (profile.name != null && profile.name.contains(" ")) {
                for (int i = 0; i < profile.name.length(); i++) {
                    if (profile.name.charAt(i) == ' ') {
                        profile.firstName = profile.name.substring(0, i);
                        profile.lastName = profile.name.substring((i + 1), profile.name.length());
                        break;
                    }
                }
            }
            Uri personPhoto = account.getPhotoUrl();
            if(personPhoto != null) {
                profile.imgUrl = personPhoto.toString();
            }
        }
    }

    private void fetchProfileAndAccessToken(boolean accPermission, GoogleSignInAccount signInAccount) {
        Logger.debug(TAG, "fetchProfileAndAccessToken:" ,
                "accPermission=" + accPermission + "signInAccount=" +signInAccount);
        if(signInAccount != null) {
            Account currAccount = new Account(signInAccount.getEmail(), "com.google");

            setProfileInfo(signInAccount);
            String idToken = signInAccount.getIdToken();
            identity.setIdToken(idToken);
            new GetGoogleAccessToken(currAccount).execute();
        }else {
            notifyLoginStatus(false, SocialClientListener.INTERNAL_ERROR,
                    getActivity().getString(R.string.lip_sign_up_error_toast_internal_error));
            Logger.error(ILogger.LIP_E002, TAG, "fetchProfileAndAccessToken",
                    "Account is null", new Exception());
        }
    }

    private void notifyLoginStatus(boolean isSuccess, int errorCode, String errorMessage) {
        hideProgressDialog();
        if (isSuccess) {
            ((SocialClientLoginListener)
                    localListeners.get(REQUEST_LOGIN)).onSocialLoginSuccess(identity);
        } else {
            localListeners.get(REQUEST_LOGIN).onSocialLoginError(errorCode, errorMessage);
        }
        requestLogout();
    }

    private void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
        }
        dialog.setProgressTitle(getActivity().getString(R.string.lip_sign_up_connecting_google));
        dialog.show();
    }

    private void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private class GetGoogleAccessToken extends AsyncTask<Void, Void, String> {
        private final Account currAccount;

        GetGoogleAccessToken(Account account) {
            this.currAccount = account;
        }

        @Override
        protected String doInBackground(Void... params) {
            /** The scopes required for google login */
            final String SCOPES = "oauth2:" + "email profile";
            try {
                return GoogleAuthUtil.getToken(getActivity(), currAccount, SCOPES);
            } catch (IOException e) {
                return null;
            } catch (GoogleAuthException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            identity.setAccessToken(response);
            if (identity.getAccessToken() == null || identity.getIdToken() == null) {
                notifyLoginStatus(false, SocialClientListener.INTERNAL_ERROR,
                        getActivity().getString(R.string.lip_sign_up_error_toast_internal_error));
                Logger.error(ILogger.LIP_E002, TAG, "GetGoogleAccessToken:onPostExecute",
                        "Unable to fetch tokens", new Exception());
            } else {
                notifyLoginStatus(true, SocialClientListener.SUCCESS, null);
                Logger.info(TAG, "GetGoogleAccessToken:onPostExecute", "Fetched access token from Google");
            }
            hideProgressDialog();
        }
    }
}

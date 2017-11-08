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
package com.logitech.lip.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.account.AccountManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.SocialIdentity;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.IListener;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.profile.SocialProfile;
import com.logitech.lip.ui.PasswordResetDialogFragment;
import com.logitech.lip.ui.SocialClient;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.common.CustomToast;
import com.logitech.lip.ui.common.ProgressDialog;
import com.logitech.lip.ui.common.TypefaceManager;
import com.logitech.lip.ui.social.listeners.SocialClientLoginListener;
import com.logitech.lip.utility.JWTokenDecoder;

import java.lang.ref.WeakReference;


public final class LoginSelectorFragment extends BaseFragment implements
        View.OnClickListener, SocialClientLoginListener {

    private static final String TAG = LoginSelectorFragment.class.getSimpleName();

    private Activity parentActivity;
    private UserInfo userInfo;
    private boolean initMode;
    private boolean isSocial;

    private SocialLoginController controller;
    private TypefaceManager typefaceManager;
    private ProgressDialog progressDialog;

    private LoginOptions loginOptions;

    private boolean isRestoredBackStack = false;

    private LoginSelectorUiNavigationListener uiNavigationListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            parentActivity = getActivity();
            uiNavigationListener = (LoginSelectorUiNavigationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement LoginSelectorUiNavigationListener");
        }

        if (loginOptions != null) {
            initMode = loginOptions.isCreate();
            isSocial = loginOptions.isSocialLogin();
            boolean isPersist = loginOptions.isPersistToken();

            userInfo = new UserInfo(null, null, initMode);
            // update token persist or not based on LoginOptions
            userInfo.setIsPersist(isPersist);
            userInfo.setCreate(initMode);
            userInfo.setVerifyEmail(LIPSdk.isVerifyEmail());
        }
        controller = new SocialLoginController();
        typefaceManager = TypefaceManager.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRestoredBackStack = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.lip_frag_signin_selector, container, false);

        initTitleBar(rootView);
        initUI(rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_facebook) {
            controller.requestLogin(parentActivity, SocialClient.Provider.PROVIDER_FACEBOOK, this);
            Logger.info(TAG, "onClick", "Facebook button is clicked");
        } else if (v.getId() == R.id.btn_google) {
            controller.requestLogin(parentActivity, SocialClient.Provider.PROVIDER_GOOGLE, this);
            Logger.info(TAG, "onClick", "Google button is clicked");
        } else if (v.getId() == R.id.btn_email) {
            if (initMode) {
                uiNavigationListener.showSignUpScreen();
                Logger.info(TAG, "onClick", "Email button is clicked for creating a new account");
            } else {
                uiNavigationListener.showSignInScreen();
                Logger.info(TAG, "onClick", "Email button is clicked for logging into an existing account");
            }
        }
    }

    public void setUserData(LoginOptions loginOptions) {
        this.loginOptions = loginOptions;
    }

    private void initTitleBar(View rootView) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        TextView titleView  = titleBar.getTitleView();

        // Set Default font to Title TextView
        if(typefaceManager != null) {
            titleView.setTypeface(typefaceManager.getDefaultTypeface(parentActivity, Typeface.BOLD));
        }

        if (initMode) {
            titleBar.setTitle(R.string.lip_sign_up_create_id);

        } else {
            titleBar.setTitle(R.string.lip_sign_up_login);
        }

        titleBar.setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiNavigationListener.showPreviousScreen();
            }
        });
    }

    private void initUI(View rootView) {
        final ImageButton socialGoogle = (ImageButton) rootView.findViewById(R.id.btn_google);
        final ImageButton socialFacebook = (ImageButton) rootView.findViewById(R.id.btn_facebook);
        final Button logiEmail = (Button) rootView.findViewById(R.id.btn_email);

        final LinearLayout buttonLayout = (LinearLayout)rootView.findViewById(R.id.signinLayout);
        // Set Default font to Email button
        if(typefaceManager != null) {
            logiEmail.setTypeface(typefaceManager.getDefaultTypeface(parentActivity, Typeface.BOLD));
        }
        socialFacebook.setOnClickListener(this);
        logiEmail.setOnClickListener(this);
        socialGoogle.setOnClickListener(this);

        TextView loginDesc = (TextView) rootView.findViewById(R.id.loginDesc);

        // Set Default font to loginDesc
        if(typefaceManager !=null) {
            loginDesc.setTypeface(typefaceManager.getDefaultTypeface(parentActivity, Typeface.BOLD));
        }

        if (initMode) {
            loginDesc.setText(getResources().getString(R.string.lip_sign_up_with));
        } else {
            loginDesc.setText(getResources().getString(R.string.lip_login_with));
        }

        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setProgressTitle(parentActivity.getString(R.string.lip_sign_up_account_signin_progress));

        // Not supported social login
        if(!isSocial) {
            socialGoogle.setVisibility(View.GONE);
            socialFacebook.setVisibility(View.GONE);

            // rootView.findViewById(R.id.dividerEmail).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.dividerGoogle).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.dividerFacebook).setVisibility(View.INVISIBLE);
        }
        // In Change isChangeClaimsMode(fetchCredentials) mode we need to choose earlier login option
        if(!isRestoredBackStack && loginOptions != null && loginOptions.isChangeClaimsMode()) {
            enableDisableView(buttonLayout, false);
            AccountManager.getCurrentAccountToken(false, new ResponseListener<AccountToken>() {
                @Override
                public void onSuccess(AccountToken result) {
                    enableDisableView(buttonLayout, true);

                    if (result == null) return;

                    SocialIdentity social = result.getSocial();
                    if (social != null) {
                        if (social.getProvider().equalsIgnoreCase(
                                SocialClient.Provider.PROVIDER_FACEBOOK)) {
                            socialFacebook.performClick();
                        } else if (social.getProvider().equalsIgnoreCase(SocialClient.Provider.PROVIDER_GOOGLE)){
                            socialGoogle.performClick();
                        }
                    } else {
                        logiEmail.performClick();
                    }
                }

                @Override
                public void onError(ErrorCode errorCode, String errorMessage) {
                    enableDisableView(buttonLayout, true);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != PasswordResetDialogFragment.ERROR_PASSWORD_RESET) {
            controller.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
            // Assumption we always have a token in this scenario
            if(parentActivity != null && uiNavigationListener.getCurrentAccountToken() != null) {
                AccountToken token = uiNavigationListener.getCurrentAccountToken();
                boolean touAccepted = JWTokenDecoder.isTouAccepted(token.getIdToken());
                if(!touAccepted) {
                    uiNavigationListener.showTouOptInScreen(userInfo);
                } else {
                    uiNavigationListener.userLoginComplete(true);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        controller.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissCustomToast();
        dismissProgressDialog();

        isRestoredBackStack = true;
    }

    @Override
    public void onSocialLoginSuccess(final SocialIdentity identity) {
        Logger.info(TAG, "onSocialLoginSuccess", "onSocialLoginSuccess");
        SocialProfile profile = controller.getSelectedClient().getProfile();
        // Verify login Email match exactly or not
        String loginEmail, profileEmail;
        loginEmail = profileEmail = null;

        if (loginOptions != null) {
            loginEmail = loginOptions.getEmail();
        }

        if (profile != null) {
            profileEmail = profile.getEmail();
        }

        // Launch Sign-In fragment with Email if not matched
        if (!initMode && loginEmail != null && !loginEmail.equalsIgnoreCase(profileEmail)) {
            uiNavigationListener.showSignInScreen();
        } else {
            userInfo = new UserInfo(profile, identity);
            if (loginOptions != null) {
                // update token persist or not based on LoginOptions
                boolean isPersist = loginOptions.isPersistToken();
                userInfo.setIsPersist(isPersist);
                userInfo.setCreate(initMode);
                userInfo.setVerifyEmail(LIPSdk.isVerifyEmail());
            }

            showProgressDialog(parentActivity.getString(R.string.lip_sign_up_account_signin_progress));
            new RequestLipService(this, userInfo).requestSignIn();

            Logger.info(TAG, "onSocialLoginSuccess", "Logging in the user");
        }
    }

    @Override
    public void onSocialLoginError(int errorCode, String errorMessage) {
        Logger.debug(TAG, "onSocialLoginError", "errorCode : " + errorCode + " errorMessage:" + errorMessage);
        SocialProfile profile = controller.getSelectedClient().getProfile();
        updateUserInfo(profile, null);
        if (errorMessage != null) {
            showCustomToast(errorMessage);
        }
    }

    private void updateUserInfo(SocialProfile profile, SocialIdentity identity) {
        // Update the Social identity details
        uiNavigationListener.updateCurrentUserInfo(identity);
        uiNavigationListener.updateCurrentUserInfo(profile);
    }

    private void showCustomToast(String errMsg) {
        dismissCustomToast();

        customToast = new CustomToast(parentActivity);
        customToast.showToast(errMsg);
    }

    private void dismissCustomToast() {
        if(customToast != null) {
            customToast.cancel();
        }
    }

    public void dismissProgressDialog() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showProgressDialog(String title) {
        if(progressDialog != null) {
            progressDialog.setProgressTitle(title);
            progressDialog.show();
        }
    }

    private void onLoginSuccess(AccountToken accountToken) {
        Logger.debug(LoginSelectorFragment.TAG, "onSuccess", "Login success");
        uiNavigationListener.updateLoginResponse(accountToken, false, false);
        uiNavigationListener.updateCurrentUserInfo(userInfo);
        dismissProgressDialog();
        if (loginOptions != null && loginOptions.isValidate()) {
            UserInfo claimsInfo = JWTokenDecoder.getUserInfo(accountToken.getIdToken());
            boolean touAccepted = JWTokenDecoder.isTouAccepted(accountToken.getIdToken());
            if(accountToken.isPasswordReset()){
                // The password is deleted on un-verified account during social login
                uiNavigationListener.showPasswordResetScreen(this);
            } else if(!touAccepted) {
                uiNavigationListener.showTouOptInScreen(userInfo);
            } else if ( LIPSdk.isVerifyEmail() &&
                    !(accountToken.isEmailVerified() ||
                            claimsInfo.isEmailStatus()) && userInfo.getSocial() == null) {
                uiNavigationListener.showEmailVerifyFragmentScreen();
            } else {
                uiNavigationListener.userLoginComplete(true);
            }
        } else{
            uiNavigationListener.userLoginComplete(true);
        }
    }

    private void onLoginError(IListener.ErrorCode errorCode, String errorMsg) {
        dismissProgressDialog();
        if (loginOptions != null && loginOptions.isValidate()) {
            uiNavigationListener.showErrorScreen(errorCode, errorMsg);
        } else {
            uiNavigationListener.updateCurrentUserInfo(userInfo);
            uiNavigationListener.updateLoginResponse(errorCode, errorMsg);
            uiNavigationListener.userLoginComplete(false);
        }
    }

    private void enableDisableView(View view, boolean enabled) {
        if(view == null ) return;

        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;
            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public interface LoginSelectorUiNavigationListener extends
            UserLoginInfoListener , BackNavigationListener {
        void showSignInScreen();
        void showSignUpScreen();
        void showPreviousScreen();
        void showTouOptInScreen(UserInfo userInfo);
        void showPasswordResetScreen(Fragment targetFragment);
        void showEmailVerifyFragmentScreen();

        void showErrorScreen(IListener.ErrorCode errorCode, String errorMsg);
    }

    private static class RequestLipService extends ResponseListener<AccountToken> {
        private final UserInfo userInfo;
        private final WeakReference<Fragment> weakFragment;

        RequestLipService(Fragment fragment, UserInfo userInfo){
            this.userInfo = userInfo;
            weakFragment = new WeakReference<>(fragment);
        }

        void requestSignIn(){
            Logger.debug(LoginSelectorFragment.TAG, "requestSignIn", "Logging into : " + userInfo.getEmail());
            com.logitech.lip.account.AccountManager.signIn(userInfo, this);
        }

        @Override
        public void onSuccess(AccountToken result) {
            Logger.debug(LoginSelectorFragment.TAG, "onSuccess", "Login success");
            LoginSelectorFragment fragment = (LoginSelectorFragment) weakFragment.get();
            if (fragment != null && !fragment.isDetached()) {
                fragment.onLoginSuccess(result);
            }
        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            Logger.debug(LoginSelectorFragment.TAG, "onError",
                    "errorCode : " + errorCode + " errorMessage :" + errorMessage);
            LoginSelectorFragment fragment = (LoginSelectorFragment) weakFragment.get();
            if (fragment != null && !fragment.isDetached() ) {
                fragment.onLoginError(errorCode, errorMessage);
            }
        }
    }
}

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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.LoginController;
import com.logitech.lip.LoginListener;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.SocialIdentity;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.IListener;
import com.logitech.lip.profile.SocialProfile;
import com.logitech.lip.ui.ErrorFragment;
import com.logitech.lip.ui.PasswordResetDialogFragment;
import com.logitech.lip.ui.SocialClient;
import com.logitech.lip.ui.login.email.EmailVerifyFragment;
import com.logitech.lip.ui.login.email.ForgotPasswordFragment;
import com.logitech.lip.ui.login.email.SignInFragment;
import com.logitech.lip.ui.login.email.SignUpFragment;
import com.logitech.lip.ui.login.email.TermsAndConditionFragment;
import com.logitech.lip.ui.login.email.TermsWebViewFragment;


/**
 * Only activity to handle complete flow of SignIn/SignUp.
 * <p>
 * Not using Interface concepts to receive data from fragments as the extending scope is limited.
 * Instead we are using direct Activity public API's with data.
 */

public class LoginSelectorActivity extends BaseActivity implements
        SignUpFragment.SignUpUiNavigationListener,
        SignInFragment.SignInUiNavigationListener,
        TermsAndConditionFragment.TermsConditionUiNavigationListener,
        ForgotPasswordFragment.ForgotPasswordUiNavigationListener,
        BackNavigationListener,
        LoginSelectorFragment.LoginSelectorUiNavigationListener {

    public static final String FRAGMENT_TAG = "LogInSelector";

    public static final String LOGIN_OPTIONS = "LOGIN_OPTIONS";

    private static final String TAG = LoginSelectorActivity.class.getSimpleName();


    private UserInfo userInfo = new UserInfo(null, null, false);
    private LoginOptions loginOptions;
    private LogInResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lip_activity_login_selector);

        if (getWindow() != null && LIPSdk.getAppConfiguration() != null
                && LIPSdk.getAppConfiguration().isKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        setDataFromIntent();

        if (findViewById(R.id.loginContainerHolder) != null) {
            if (savedInstanceState != null) {
                return;
            }
            // Create a new Fragment to be placed in the activity layout
            LoginSelectorFragment selectorFragment = new LoginSelectorFragment();
            selectorFragment.setUserData(loginOptions);

            // Add the fragment to the 'fragment_container' FrameLayout
            addFragment(selectorFragment, false, FRAGMENT_TAG);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setDataFromIntent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        executePendingUIRequests();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null && !fragment.isDetached()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null && !fragment.isDetached()) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            LoginListener listener = LoginController.getInstance().getListener();
            if (listener != null) {
                listener.onLoginError(null, 0, getString(R.string.lip_sign_up_error_toast_cancel_error));
            }
        } else {
            if (isActivityResumed()) {
                getSupportFragmentManager().popBackStack();
            } else {
                PendingUIRequest uiRequest = new PendingUIRequest();
                uiRequest.isPopBackstack = true;
                addPendingUIRequest(uiRequest);
            }
        }
    }

    public UserInfo getCurrentUserInfo() {
        return userInfo;
    }

    /**
     * Reset specific info which is not required to expose to client
     */
    private void refineUserInfo() {
        userInfo.setChannelId(null);
        userInfo.setClientId(null);
        // userInfo.setSocial(null);
    }

    /**
     * Sets the focus to the view passed and opens up the keyboard
     *
     * @param view Sets focus to the view
     */
    public void setFocus(TextView view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Closes the keyboard
     */
    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusWindow = getWindow().getCurrentFocus();
        if (focusWindow != null) {
            inputMethodManager.hideSoftInputFromWindow(focusWindow.getWindowToken(), 0);
        }
    }

    /**
     * Adds the fragment in UI
     *
     * @param fragment       Fragment to be replaced
     * @param addToBackStack Flag to indicate if the fragment is to be added to the backstack
     * @param tag            Tag of the fragment
     */
    public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {

        PendingUIRequest uiRequest = new PendingUIRequest();
        uiRequest.fragment = fragment;
        uiRequest.isReplace = false;
        uiRequest.tag = tag;
        uiRequest.addToBackStack = addToBackStack;

        if (isActivityResumed()) {
            executeInternal(uiRequest);
        } else {
            addPendingUIRequest(uiRequest);
        }
    }

    /**
     * Replaces the fragment in UI
     *
     * @param fragment       Fragment to be replaced
     * @param addToBackStack Flag to indicate if the fragment is to be added to the backstack
     * @param tag            Tag of the fragment
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack, String tag) {
        PendingUIRequest uiRequest = new PendingUIRequest();
        uiRequest.fragment = fragment;
        uiRequest.isReplace = true;
        uiRequest.tag = tag;
        uiRequest.addToBackStack = addToBackStack;

        if (isActivityResumed()) {
            executeInternal(uiRequest);
        } else {
            addPendingUIRequest(uiRequest);
        }

    }

    /**
     * Replaces the fragment in UI allowing a state loss
     *
     * @param fragment       Fragment to be replaced
     * @param addToBackStack Flag to indicate if the fragment is to be added to the backstack
     * @param tag            Tag of the fragment
     */
    public void replaceFragmentAllowingStateLoss(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.loginContainerHolder, fragment, tag);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Sets the data from bundle.
     * This data is obtained from the client app and is used across the lip sdk
     */
    private void setDataFromIntent() {
        if (getIntent() != null) {
            loginOptions = getIntent().getParcelableExtra(LOGIN_OPTIONS);

            //Create with default options
            if(loginOptions == null) {
                loginOptions = new LoginOptions.Builder().build();
            }
        }
    }

    protected void executeInternal(PendingUIRequest p) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (p.isPopBackstack) {
            fm.popBackStack();
        } else if (p.fragment != null) {
            if (p.isReplace) {
                fragmentTransaction.replace(R.id.loginContainerHolder, p.fragment, p.tag);
                if (p.addToBackStack) {
                    fragmentTransaction.addToBackStack(p.tag);
                }
            } else {
                fragmentTransaction.add(R.id.loginContainerHolder, p.fragment, p.tag);
                if (p.addToBackStack) {
                    fragmentTransaction.addToBackStack(p.tag);
                }
            }
            fragmentTransaction.commit();
        }
    }

    public void showPasswordResetDialog(Fragment fragment, int requestCode, Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);

        PasswordResetDialogFragment errorFragment = PasswordResetDialogFragment.newInstance(args);
        errorFragment.setTargetFragment(fragment, requestCode);
        errorFragment.show(fm, TAG);
    }

    @Override
    public void showSignInScreen() {
        Logger.debug(TAG, "launchSignInFragment", "Launching Sign in fragment");
        SignInFragment signInFragment = SignInFragment.newInstance(loginOptions);
        replaceFragment(signInFragment, true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showTouOptInScreen(UserInfo userInfo) {
        Logger.info(TAG, "launchTermsAndConditionFragment", "Launching terms and conditions page");
        TermsAndConditionFragment fragment = TermsAndConditionFragment.newInstance(loginOptions, userInfo);

        replaceFragment(fragment, true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showSignUpScreen() {
        Logger.info(TAG, "launchSignUpFragment", "Launching create account / signup page");
        SignUpFragment signUpFragment = SignUpFragment.newInstance(loginOptions);
        replaceFragment(signUpFragment, true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showEmailVerifyFragmentScreen() {
        Logger.info(TAG, "launchEmailVerifyFragment", "Launching EmailVerifyFragment");
        replaceFragment(EmailVerifyFragment.newInstance(), true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showTermsWebViewScreen(int urlType, String url) {
        Logger.info(TAG, "showTermsWebViewScreen", "Launching TermsWebViewScreen");
        TermsWebViewFragment fragment = TermsWebViewFragment.newInstance(urlType, url);
        replaceFragment(fragment, true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showForgotPasswordScreen(String email) {
        Logger.info(TAG, "showForgotPasswordScreen", "Launching forgot password screen");
        ForgotPasswordFragment fragment = ForgotPasswordFragment.newInstance(email);
        replaceFragment(fragment, true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showPreviousScreen() {
        onBackPressed();
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusWindow = getWindow().getCurrentFocus();
        if (focusWindow != null) {
            inputMethodManager.hideSoftInputFromWindow(focusWindow.getWindowToken(), 0);
        }
    }

    @Override
    public void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void userLoginComplete(boolean isSuccess) {
        LoginListener listener = LoginController.getInstance().getListener();
        refineUserInfo();
        if (response != null && listener != null) {
            if (isSuccess) {
                listener.onLoginSuccess(userInfo, response.accountToken);
            } else {
                listener.onLoginError(userInfo, response.errorCode.ordinal(), response.errorMsg);
            }
        }
        closeKeyboard();
        finish();
    }

    @Override
    public void updateCurrentUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void updateCurrentUserInfo(String email, String password) {
        userInfo.setEmail(email);
        userInfo.setPassword(password);
    }

    @Override
    public void updateCurrentUserInfo(SocialIdentity identity) {
        userInfo.setSocial(identity);
    }

    @Override
    public void updateCurrentUserInfo(SocialProfile socialProfile) {
        if (socialProfile != null) {
            userInfo.setEmail(socialProfile.email);
            userInfo.setLastName(socialProfile.getName());
            userInfo.setPicture(socialProfile.getImgUrl());
        }
    }

    @Override
    public AccountToken getCurrentAccountToken() {
        if (response != null) {
            return response.getAccountToken();
        }
        return null;
    }

    @Override
    public void updateLoginResponse(final AccountToken accountToken,
                                    boolean isTermsAccepted, boolean keepMe) {
        response = new LogInResponse(accountToken, isTermsAccepted, keepMe);
    }

    @Override
    public void updateLoginResponse(IListener.ErrorCode errorCode, String errorMsg) {
        response = new LogInResponse(errorCode, errorMsg);
    }

    @Override
    public void showErrorScreen(IListener.ErrorCode errorCode, String errorMsg) {
        Bundle args = new Bundle();
        args.putString(ErrorFragment.HEADER_TITLE, getString(R.string.lip_sign_up_access_denied));

        args.putString(ErrorFragment.ERROR_DESC, errorMsg);
        args.putString(ErrorFragment.POSITIVE_BUTTON, getString(R.string.lip_error_ok));
        args.putInt(ErrorFragment.REQ_ERROR_CODE, errorCode.ordinal());

        // activity.showErrorDialog(fragment, errorCode, args);
        ErrorFragment errorFragment = new ErrorFragment();
        errorFragment.setArguments(args);
        replaceFragment(errorFragment, true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showErrorScreen(int errorCode, String errorMsg) {
        Bundle args = new Bundle();
        if (errorCode != 0) {
            args.putString(ErrorFragment.HEADER_TITLE, getString(R.string.lip_sign_up_access_denied));
        } else {
            args.putString(ErrorFragment.HEADER_TITLE, getString(R.string.lip_sign_up_forgot_password_title));
        }
        args.putString(ErrorFragment.ERROR_DESC, errorMsg);

        args.putString(ErrorFragment.POSITIVE_BUTTON, getString(R.string.lip_error_ok));
        args.putInt(ErrorFragment.REQ_ERROR_CODE, errorCode);

        // activity.showErrorDialog(fragment, errorCode, args);
        ErrorFragment errorFragment = new ErrorFragment();
        errorFragment.setArguments(args);
        replaceFragment(errorFragment, true, LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Override
    public void showPasswordResetScreen(Fragment targetFragment) {
        Bundle args = new Bundle();
        String text = new String();
        String provider = SocialClient.Provider.PROVIDER_NONE;
        if (userInfo.getSocialIdentity() != null) {
            provider = userInfo.getSocialIdentity().getProvider();
        }
        if (provider.equals(SocialClient.Provider.PROVIDER_FACEBOOK)) {
            text = getString(R.string.lip_sign_up_facebook);
        } else if (provider.equals(SocialClient.Provider.PROVIDER_GOOGLE)) {
            text = getString(R.string.lip_sign_up_google);
        }

        args.putString(ErrorFragment.HEADER_TITLE, getString(R.string.lip_sign_up_password_reset));
        args.putString(ErrorFragment.ERROR_DESC, getString(R.string.lip_signin_error_password_deleted, text));
        args.putString(ErrorFragment.POSITIVE_BUTTON, getString(R.string.lip_error_ok));
        args.putInt(ErrorFragment.REQ_ERROR_CODE, PasswordResetDialogFragment.ERROR_PASSWORD_RESET);

        showPasswordResetDialog(targetFragment, PasswordResetDialogFragment.ERROR_PASSWORD_RESET,
                args);
    }

    private class LogInResponse {
        private AccountToken accountToken;
        private boolean keepMeUpdate;
        private boolean isTermsAccepted;

        private IListener.ErrorCode errorCode;
        private String errorMsg;

        LogInResponse(AccountToken accountToken, boolean isTermsAccepted,
                      boolean keepMeUpdate) {
            this.accountToken = accountToken;
            this.isTermsAccepted = isTermsAccepted;
            this.keepMeUpdate = keepMeUpdate;
        }

        LogInResponse(IListener.ErrorCode errorCode,
                      String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }

        AccountToken getAccountToken() {
            return accountToken;
        }
    }


}

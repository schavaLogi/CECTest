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
package com.logitech.lip.ui.login.email;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.IListener;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.ui.PasswordResetDialogFragment;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.common.CustomToast;
import com.logitech.lip.ui.common.ProgressDialog;
import com.logitech.lip.ui.login.BackNavigationListener;
import com.logitech.lip.ui.login.BaseFragment;
import com.logitech.lip.ui.login.KeyboardCommandListener;
import com.logitech.lip.ui.login.UserLoginInfoListener;
import com.logitech.lip.utility.JWTokenDecoder;

import java.lang.ref.WeakReference;

public class SignInFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = SignInFragment.class.getSimpleName();
    private Activity parentActivity;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private ImageView showPassword;
    private UserInfo userInfo;
    private LoginOptions loginOptions;
    private SignInUiNavigationListener uiNavigationListener;
    private ProgressDialog progressDialog;
    /**
     * Variable is declared globally to enable UI testing using Espresso
     */
    private String errResId;

    public static SignInFragment newInstance(LoginOptions loginOptions) {
        SignInFragment signInFragment = new SignInFragment();
        signInFragment.setUserData(loginOptions);
        return signInFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            uiNavigationListener = (SignInUiNavigationListener) getActivity();
            parentActivity = getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement SignInUiNavigationListener");
        }

        userInfo = new UserInfo(null, null, false);

        if (loginOptions != null) {
            // update token persist or not based on LoginOptions
            boolean isPersist = loginOptions.isPersistToken();
            userInfo.setIsPersist(isPersist);

            // SignIn mode and passed default email
            String email = loginOptions.getEmail();
            userInfo.setEmail(email);

            userInfo.setVerifyEmail(LIPSdk.isVerifyEmail());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lip_frag_sign_in, container, false);
        initUiControls(rootView);

        initTitleBar(rootView);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiNavigationListener.hideKeyboard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissCustomToast();
        dismissProgressDialog();
    }

    private void setUserData(LoginOptions loginOptions) {
        this.loginOptions = loginOptions;
    }

    private void initTitleBar(View rootView) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        titleBar.setTitle(R.string.lip_sign_up_login)
                .setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uiNavigationListener.showPreviousScreen();
                    }
                });
    }

    private void initUiControls(View rootView) {

        emailView = (AutoCompleteTextView) rootView.findViewById(R.id.sign_in_email);
        passwordView = (EditText) rootView.findViewById(R.id.sign_in_password);
        /* While setting the input type Android internally sets the font-family of the view.
         * Hence the typeface of email view is obtained and set to password view. */
        passwordView.setTypeface(emailView.getTypeface());
        Button submitButton = (Button) rootView.findViewById(R.id.sign_in_submit);

        RelativeLayout showPasswordContainer = (RelativeLayout) rootView.findViewById(R.id.sign_in_show_password_container);
        showPassword = (ImageView) rootView.findViewById(R.id.sign_in_show_password);
        showPassword.setTag(false);
        showPasswordContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = (boolean) showPassword.getTag();
                showPassword(showPassword, !isChecked);
                showPassword.setTag(!isChecked);
            }
        });

        submitButton.setOnClickListener(this);

        TextView needAccount = (TextView) rootView.findViewById(R.id.sign_in_need_account);
        needAccount.setOnClickListener(this);
        if (loginOptions != null) {
            boolean isCreateFlow = loginOptions.isCreate();
            if (!isCreateFlow) {
                needAccount.setVisibility(View.GONE);
            }
        }

        TextView forgotPassword = (TextView) rootView.findViewById(R.id.sign_in_forgot);
        forgotPassword.setOnClickListener(this);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_DONE == actionId) {
                    validateAndSignIn();
                    return true;
                }
                return false;
            }
        });

        if (!TextUtils.isEmpty(userInfo.getEmail())) {
            // In-case if Email is valid disable editing
            if (Patterns.EMAIL_ADDRESS.matcher(userInfo.getEmail()).matches()) {
                if (loginOptions != null) {
                    String email = loginOptions.getEmail();
                    if (email != null && email.equalsIgnoreCase(userInfo.getEmail())) {
                        emailView.setClickable(false);
                        emailView.setFocusable(false);
                        emailView.setFocusableInTouchMode(false);
                        emailView.setCursorVisible(false);
                    }
                }
            }
            emailView.setText(userInfo.getEmail());
            uiNavigationListener.showKeyboard(passwordView);
        } else {
            uiNavigationListener.showKeyboard(emailView);
        }

        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setCancelable(false);
        progressDialog.setProgressTitle(parentActivity.getString(R.string.lip_sign_up_account_signin_progress));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_need_account) {
            uiNavigationListener.showPreviousScreen();

            uiNavigationListener.showSignUpScreen();
        } else if (v.getId() == R.id.sign_in_forgot) {
            launchForgotPasswordFragment();
        } else if (v.getId() == R.id.sign_in_submit) {
            validateAndSignIn();
            uiNavigationListener.hideKeyboard();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PasswordResetDialogFragment.ERROR_PASSWORD_RESET:
                if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                    // Assumption we always have a token in this scenario
                    if(uiNavigationListener != null && uiNavigationListener.getCurrentAccountToken() != null) {
                        AccountToken token = uiNavigationListener.getCurrentAccountToken();
                        boolean touAccepted = JWTokenDecoder.isTouAccepted(token.getIdToken());
                        if(!touAccepted) {
                            uiNavigationListener.showTouOptInScreen(userInfo);
                        } else {
                            uiNavigationListener.userLoginComplete(true);
                        }
                    }
                }
                break;
        }
    }

    /**
     * This method is used to get the error id for UI testing using Espresso
     */
    public String getErrResId() {
        return errResId;
    }

    private void validateAndSignIn() {
        String email = emailView.getText().toString().trim();
        String passWord = passwordView.getText().toString();

        // In Special case we shouldnt validate email format, without password also can proceed
        if (!TextUtils.isEmpty(email)) {
            userInfo.setEmail(email);
            if (email.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                if (!TextUtils.isEmpty(passWord)) {
                    userInfo.setPassword(passWord);

                    showProgressDialog(parentActivity.getString(
                                R.string.lip_sign_up_account_signin_progress));
                    new RequestLipService(this, userInfo).requestSignIn();

                } else {
                    if (loginOptions != null && loginOptions.isValidate()) {
                        customToast = new CustomToast(parentActivity);
                        errResId = getString(R.string.lip_sign_up_error_toast_enter_pass);
                        customToast.showToast(errResId);
                    } else {
                        uiNavigationListener.updateCurrentUserInfo(email, passWord);
                        uiNavigationListener.updateLoginResponse(IListener.ErrorCode.ERROR_CODE_INTERNAL, "");
                        uiNavigationListener.userLoginComplete(false);
                    }
                }
            } else {
                // Email is not in standard format
                if (loginOptions!= null && loginOptions.isValidate()) {
                    customToast = new CustomToast(parentActivity);
                    errResId = getString(R.string.lip_sign_up_error_toast_invalid_email);
                    customToast.showToast(errResId);
                } else {
                    if (!TextUtils.isEmpty(passWord)) {
                        userInfo.setPassword(passWord);
                    }
                    uiNavigationListener.updateCurrentUserInfo(email, passWord);
                    uiNavigationListener.updateLoginResponse(IListener.ErrorCode.ERROR_CODE_INTERNAL, "");
                    uiNavigationListener.userLoginComplete(false);
                }
            }
        } else {
            showCustomToast(getString(R.string.lip_sign_up_error_toast_invalid_email));
        }

    }

    private void showPassword(View view, boolean isVisible) {
        if (!TextUtils.isEmpty(passwordView.getText())) {
            if (isVisible) {
                passwordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD |
                        InputType.TYPE_CLASS_TEXT);
                passwordView.setTypeface(emailView.getTypeface());
                view.setBackgroundResource(R.drawable.lip_show_password_off);
            } else {
                passwordView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                        InputType.TYPE_CLASS_TEXT);
                passwordView.setTypeface(emailView.getTypeface());
                view.setBackgroundResource(R.drawable.lip_show_password_on);
            }
            passwordView.setSelection(passwordView.length());
        }
    }

    private void launchForgotPasswordFragment() {
        Logger.info(TAG, "launchSignUpFragment", "Launching forgot password page");
        String email = emailView.getText().toString();
        uiNavigationListener.showForgotPasswordScreen(email);
    }

    private void showCustomToast(String errMsg) {
        dismissCustomToast();
        errResId = errMsg;
        customToast = new CustomToast(parentActivity);
        customToast.showToast(errMsg);
    }

    private void dismissCustomToast() {
        if (customToast != null) {
            customToast.cancel();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog(String title) {
        if (progressDialog != null) {
            progressDialog.setProgressTitle(title);
            progressDialog.show();
        }
    }

    private void onSignInError(IListener.ErrorCode errorCode, String errorMessage) {
        dismissProgressDialog();
        Logger.debug(SignInFragment.TAG, "onSignInError", "Login error =" + loginOptions);
        if (loginOptions != null) {
            if (loginOptions.isValidate()) {
                uiNavigationListener.updateLoginResponse(errorCode, errorMessage);
                uiNavigationListener.showErrorScreen(errorCode, errorMessage);
            } else {
                uiNavigationListener.updateCurrentUserInfo(userInfo);
                uiNavigationListener.updateLoginResponse(errorCode, errorMessage);
                uiNavigationListener.userLoginComplete(false);
            }
        }
    }

    private void onSignInSuccess(AccountToken accountToken) {

        uiNavigationListener.updateLoginResponse(accountToken, false, false);
        uiNavigationListener.updateCurrentUserInfo(userInfo);
        dismissProgressDialog();
        if (loginOptions != null && loginOptions.isValidate()) {
            UserInfo claimsInfo = JWTokenDecoder.getUserInfo(accountToken.getIdToken());
            boolean touAccepted = JWTokenDecoder.isTouAccepted(accountToken.getIdToken());
            if (accountToken.isPasswordReset()) {
                uiNavigationListener.showPasswordResetScreen(this);
            } else if (!touAccepted) {
                uiNavigationListener.showTouOptInScreen(userInfo);
            } else if (LIPSdk.isVerifyEmail() && !(accountToken.isEmailVerified() ||
                    claimsInfo.isEmailStatus()) && userInfo.getSocial() == null) {
                uiNavigationListener.showEmailVerifyFragmentScreen();
            } else {
                uiNavigationListener.userLoginComplete(true);
             }
        } else {
            uiNavigationListener.userLoginComplete(true);
        }
    }

    public interface SignInUiNavigationListener extends KeyboardCommandListener,
            BackNavigationListener, UserLoginInfoListener {

        void showSignUpScreen();
        void showTouOptInScreen(UserInfo userInfo);
        void showEmailVerifyFragmentScreen();
        void showPasswordResetScreen(Fragment targetFragment);
        void showForgotPasswordScreen(String email);
        void showErrorScreen(IListener.ErrorCode errorCode, String errorMsg);
    }

    private static class RequestLipService extends ResponseListener<AccountToken> {
        private final UserInfo userInfo;
        private final WeakReference<Fragment> weakFragment;
        RequestLipService(Fragment fragment, UserInfo userInfo) {
            this.userInfo = userInfo;
            weakFragment = new WeakReference<>(fragment);
        }

        void requestSignIn() {
            Logger.debug(SignInFragment.TAG, "requestSignIn", "Logging into : " + userInfo.getEmail());
            com.logitech.lip.account.AccountManager.signIn(userInfo, this);
        }

        @Override
        public void onSuccess(AccountToken result) {
            Logger.debug(SignInFragment.TAG, "onSuccess", "Login success");
            SignInFragment fragment = (SignInFragment) weakFragment.get();
            if (fragment != null && !fragment.isDetached()) {
                fragment.onSignInSuccess(result);
             }

        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            Logger.debug(SignInFragment.TAG, "onError", "errorCode : " + errorCode + " errorMessage :" + errorMessage);
            SignInFragment fragment = (SignInFragment) weakFragment.get();
            if (fragment != null && !fragment.isDetached()) {
                Logger.debug(SignInFragment.TAG, "fragment not detached", "fragment= " +fragment);
                fragment.onSignInError(errorCode, errorMessage);
            }
        }
    }
}

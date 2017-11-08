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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.account.AccountManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.IListener;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.common.CustomToast;
import com.logitech.lip.ui.common.ProgressDialog;
import com.logitech.lip.ui.login.BackNavigationListener;
import com.logitech.lip.ui.login.BaseFragment;
import com.logitech.lip.ui.login.UserLoginInfoListener;
import com.logitech.lip.utility.JWTokenDecoder;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class TermsAndConditionFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = TermsAndConditionFragment.class.getSimpleName();
    private CheckBox acceptCheck;
    private CheckBox keepMeCheck;
    private Activity parentActivity;
    private UserInfo userInfo = new UserInfo(null,null,true);
    private CustomToast customToast;
    /** Variable is declared globally to enable UI testing using Espresso */
    private String errResId;
    private ProgressDialog dialog;
    private LoginOptions loginOptions;
    private TermsConditionUiNavigationListener uiNavigationListener;

    public static TermsAndConditionFragment newInstance(LoginOptions loginOptions, UserInfo userInfo) {
        TermsAndConditionFragment fragment = new TermsAndConditionFragment();
        fragment.setUserData(loginOptions, userInfo);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            uiNavigationListener = (TermsConditionUiNavigationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement TermsConditionUiNavigationListener");
        }

        parentActivity = getActivity();

        if(loginOptions != null) {
            // update token persist or not based on LoginOptions
            boolean isPersist = loginOptions.isPersistToken();
            userInfo.setIsPersist(isPersist);
            userInfo.setVerifyEmail(LIPSdk.isVerifyEmail());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lip_frag_tou_opt_in, container, false);

        initTitleBar(rootView);
        initUiControls(rootView);
        initTermsConditionText(rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissCustomToast();
        dismissProgressDialog();
    }

    private void setUserData(LoginOptions loginOptions, UserInfo userInfo) {
        this.loginOptions = loginOptions;
        this.userInfo = userInfo;
    }

    private void initUiControls(View rootView) {
        final Button termsAccept = (Button) rootView.findViewById(R.id.terms_accept);

        acceptCheck = (CheckBox) rootView.findViewById(R.id.sign_up_accept_button);
        keepMeCheck = (CheckBox) rootView.findViewById(R.id.sign_up_keep_button);
        termsAccept.setOnClickListener(this);

        acceptCheck.setOnClickListener(this);
        keepMeCheck.setOnClickListener(this);

        dialog = new ProgressDialog(parentActivity);
        dialog.setCancelable(false);
        dialog.setProgressTitle(parentActivity.getString(R.string.lip_sign_up_account_create_progress));
    }

    private void initTitleBar(View rootView) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        titleBar.setTitle(getString(R.string.lip_sign_up_welcome));
        titleBar.setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uiNavigationListener != null) {
                    // Clear token when we login in current session but not accepted Tou & Opt-In
                    if (uiNavigationListener.getCurrentAccountToken() != null) {
                        uiNavigationListener.updateLoginResponse(IListener.ErrorCode.ERROR_CODE_USER_CANCEL, "");
                        AccountManager.setCurrentAccountToken(null, null);
                    }
                    uiNavigationListener.showPreviousScreen();
                }
            }
        });

    }

    private void initTermsConditionText(View rootView) {
        TextView termsView = (TextView) rootView.findViewById(R.id.sign_up_accept_checkbox_text);
        final int underlineColor = ContextCompat.getColor(parentActivity, R.color.lip_primary_white_text_color);
        final int textColor = ContextCompat.getColor(parentActivity, R.color.lip_primary_white_text_color);
        if (termsView != null) {
            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String touUrl = LIPSdk.getConfiguration().getTermsUseUrl();
                    uiNavigationListener.showTermsWebViewScreen(TermsWebViewFragment.URL_TYPE_TOU,
                            touUrl);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(underlineColor);
                }
            };

            ClickableSpan clickableSpan3 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String privacyUrl = LIPSdk.getConfiguration().getPrivacyPolicyUrl();
                    uiNavigationListener.showTermsWebViewScreen(TermsWebViewFragment.URL_TYPE_PRIVACY_POLICY,
                            privacyUrl);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(underlineColor);
                }
            };

            String textString = getString(R.string.lip_sign_up_terms_use_privacy_policy);
            String [] splitStrings = textString.split("\uFF0D", 6);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            if(splitStrings.length >0) {
                for(int i= 0; i < splitStrings.length -1; i++){
                    if(!TextUtils.isEmpty(splitStrings[i]) &&
                            (splitStrings[i].charAt(0) == '\u0340' || splitStrings[i].charAt(0) == '\u0341') ) {
                        // Removed special chat which used to detect for span link
                        SpannableString ssb = new SpannableString(splitStrings[i].substring(1));
                        if (splitStrings[i].charAt(0) == '\u0340') {
                            ssb.setSpan(clickableSpan2, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (splitStrings[i].charAt(0) == '\u0341') {
                            ssb.setSpan(clickableSpan3, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        builder.append(ssb);
                    } else {
                        SpannableString ssb = new SpannableString(splitStrings[i]);
                        builder.append(ssb);
                    }
                }
                termsView.setText(builder);
            } else {
                termsView.setText(textString);
            }
            termsView.setTextColor(textColor);
            termsView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /** This method is used to get the error id for UI testing using Espresso*/
    public String getErrResId(){
        return errResId;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.terms_accept) {
            boolean isTermsAccepted = acceptCheck.isChecked();
            boolean isKeepMeInformed = keepMeCheck.isChecked();
            // Validate the check box status
            if (!isTermsAccepted) {
                errResId = getString(R.string.lip_sign_up_error_toast_accept_conditions);
                showCustomToast(errResId);
            } else {
                // In case of social we come here only when Claims not accepted
                if(userInfo.isCreate() && userInfo.getSocial() == null) {
                    new RequestLipService(this, userInfo).requestCreateAccount(isTermsAccepted,
                            isKeepMeInformed);
                } else {
                    new RequestLipService(this, userInfo).requestChangeClaims(isTermsAccepted, isKeepMeInformed);

                    // Check for next action based on response
                    AccountToken token = uiNavigationListener.getCurrentAccountToken();
                    UserInfo claimsInfo = JWTokenDecoder.getUserInfo(token.getIdToken());
                    if (LIPSdk.isVerifyEmail() && !(token.isEmailVerified() ||
                            (claimsInfo!= null && claimsInfo.isEmailStatus()))) {
                        // Remove Terms and condition fragment as we need to show signUp screen upon back
                        uiNavigationListener.showPreviousScreen();
                        uiNavigationListener.showEmailVerifyFragmentScreen();
                    } else {
                        uiNavigationListener.userLoginComplete(true);
                    }
                }
            }
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ErrorFragment.ERROR_PASSWORD_RESET:
                if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
                    parentActivity.closeActivity(true);
                }
                break;
        }
    }*/

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
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showProgressDialog(String title) {
        if(dialog != null) {
            dialog.setProgressTitle(title);
            dialog.show();
        }
    }

    private void onSignUpSuccess(AccountToken accountToken, boolean isAccepted, boolean keepMeUpdate) {
        dismissProgressDialog();

        if(uiNavigationListener != null) {
            uiNavigationListener.updateCurrentUserInfo(userInfo);
            uiNavigationListener.updateLoginResponse(accountToken, isAccepted, keepMeUpdate);
            UserInfo claimsInfo = JWTokenDecoder.getUserInfo(accountToken.getIdToken());
            if (LIPSdk.isVerifyEmail() &&
                    !(accountToken.isEmailVerified() || (claimsInfo != null && claimsInfo.isEmailStatus()))
                    && userInfo.getSocial() == null) {
                // Remove Terms and condition fragment as we need to show signUp screen upon back
                uiNavigationListener.showPreviousScreen();

                uiNavigationListener.showEmailVerifyFragmentScreen();
            } else {
                uiNavigationListener.userLoginComplete(true);
            }
        }
    }

    private void onSignUpError(IListener.ErrorCode errorCode, String errorMsg) {
        dismissProgressDialog();
        if(uiNavigationListener != null) {
            uiNavigationListener.updateLoginResponse(errorCode, errorMsg);
            if (!isDetached()) {
                // Remove Terms and condition fragment as we need to show signUp screen upon back
                uiNavigationListener.showPreviousScreen();
                uiNavigationListener.showErrorScreen(errorCode, errorMsg);
            }
        }
    }

    public interface TermsConditionUiNavigationListener extends UserLoginInfoListener , BackNavigationListener {
        void showEmailVerifyFragmentScreen();
        void showErrorScreen(IListener.ErrorCode errorCode, String errorMsg);
        void showTermsWebViewScreen(int urlType, String url);
    }

    private static class RequestLipService extends ResponseListener<AccountToken> {
        private final WeakReference<Fragment> weakFragment;
        private final UserInfo userInfo;
        private boolean isAccepted;
        private boolean keepMeUpdate;

        public RequestLipService(Fragment fragment, UserInfo userInfo) {
            this.userInfo = userInfo;
            weakFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onSuccess(AccountToken result) {
            Logger.debug(TermsAndConditionFragment.TAG, "onSuccess", "Account created successfully");
            TermsAndConditionFragment fragment = (TermsAndConditionFragment) weakFragment.get();
            if (fragment != null && !fragment.isDetached()) {
                fragment.onSignUpSuccess(result, isAccepted, keepMeUpdate);
            }
        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            Logger.debug(TermsAndConditionFragment.TAG, "onError", "errorCode : " + errorCode + " errorMessage :" + errorMessage);
            TermsAndConditionFragment fragment = (TermsAndConditionFragment) weakFragment.get();
            if (fragment != null) {
                fragment.onSignUpError(errorCode, errorMessage);
            }
        }

        /**
         * Change claims will be done only when login and explicit change Tou & Opt-In
         */
        public void requestChangeClaims(final boolean isAccepted, final boolean keepMe) {
            TermsAndConditionFragment fragment = (TermsAndConditionFragment) weakFragment.get();
            if (fragment != null) {
                Activity activity = fragment.getActivity();
                if (activity != null) {
                    fragment.showProgressDialog(activity.getString(R.string.lip_sign_up_account_signin_progress));
                    AccountToken token = fragment.uiNavigationListener.getCurrentAccountToken();
                    // Change claims
                    Map<String, Object> claims = new HashMap<>();
                    if(LIPSdk.getAppConfiguration() != null) {
                        String touKey = LIPSdk.getAppConfiguration().getAppName() + activity.getString(R.string.tou_accepted);
                        String optInKey = LIPSdk.getAppConfiguration().getAppName() + activity.getString(R.string.optin_accepted);
                        claims.put(touKey, isAccepted);
                        claims.put(optInKey, keepMe);
                    }
                    AccountManager.changeClaims(token.getAccessToken(), claims, null);
                }
            }
        }

        public void requestCreateAccount(final boolean isAccepted, final boolean keepMe) {
            Logger.debug(TermsAndConditionFragment.TAG, "requestCreateAccount", "isAccepted : "
                    + isAccepted + " keepMe : " + keepMe);
            this.isAccepted = isAccepted;
            keepMeUpdate = keepMe;

            TermsAndConditionFragment fragment = (TermsAndConditionFragment) weakFragment.get();
            if (fragment != null) {
                Activity activity = fragment.getActivity();
                if(activity != null) {
                    fragment.showProgressDialog(activity.getString(R.string.lip_sign_up_account_create_progress));
                    Map<String, Object> claims = new HashMap<>();
                    if(LIPSdk.getAppConfiguration() != null){
                        String touKey = LIPSdk.getAppConfiguration().getAppName() + activity.getString(R.string.tou_accepted);
                        String optInKey = LIPSdk.getAppConfiguration().getAppName() + activity.getString(R.string.optin_accepted);
                        claims.put(touKey, isAccepted);
                        claims.put(optInKey, keepMeUpdate);
                    }
                    AccountManager.create(userInfo, claims, this);
                }
            }
        }
    }
}

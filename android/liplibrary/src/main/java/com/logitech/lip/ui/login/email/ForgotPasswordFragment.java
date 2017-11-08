package com.logitech.lip.ui.login.email;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.logitech.lip.Logger;
import com.logitech.lip.R;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.common.CustomToast;
import com.logitech.lip.ui.common.ProgressDialog;
import com.logitech.lip.ui.login.BackNavigationListener;
import com.logitech.lip.ui.login.BaseFragment;
import com.logitech.lip.ui.login.KeyboardCommandListener;
import com.logitech.lip.ui.login.LoginSelectorActivity;

import java.lang.ref.WeakReference;


public class ForgotPasswordFragment extends BaseFragment {

    private static final String TAG = ForgotPasswordFragment.class.getSimpleName();

    public static final String LOGIN_EMAIL = "LOGIN_EMAIL";

    private ForgotPasswordUiNavigationListener uiNavigationListener;
    private EditText emailView;
    private ProgressDialog dialog;

    public static ForgotPasswordFragment newInstance(String email) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        if (!TextUtils.isEmpty(email)) {
            Bundle bundle = new Bundle();
            bundle.putString(ForgotPasswordFragment.LOGIN_EMAIL, email);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            uiNavigationListener = (ForgotPasswordUiNavigationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ForgotPasswordUiNavigationListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lip_frag_forgot_password, container, false);
        initUiControls(rootView);

        initTitleBar(rootView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        uiNavigationListener.hideKeyboard();
        if (customToast != null) {
            customToast.cancel();
        }
    }

    private void initTitleBar(View rootView) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        titleBar.setTitle(getString(R.string.lip_sign_up_forgot_password_title))
                .setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uiNavigationListener.showPreviousScreen();
                    }
                });
    }

    private void initUiControls(View rootView) {

        emailView = (EditText) rootView.findViewById(R.id.sign_up_email_forgot);
        ((LoginSelectorActivity) getActivity()).setFocus(emailView);

        final Button sendEmailView = (Button) rootView.findViewById(R.id.sign_up_send_email);

        if (getArguments() != null) {
            String email = getArguments().getString(LOGIN_EMAIL);
            if (email != null && email.length() > 0) {
                emailView.setText(email);
                emailView.setSelection(email.length());
            }
        }

        sendEmailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailView != null) {
                    String emailTxt = emailView.getText().toString().trim();
                    if (!emailTxt.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                        customToast = new CustomToast(getActivity());
                        customToast.showToast(getString(R.string.lip_sign_up_error_toast_invalid_email));
                    } else {
                        uiNavigationListener.hideKeyboard();
                        new RequestLipService(ForgotPasswordFragment.this).requestForgotPassword(emailTxt);
                    }
                }
            }
        });

        dialog = new ProgressDialog(getActivity());

        dialog.setCancelable(false);
        dialog.setProgressTitle(getResources().getString(R.string.lip_sign_up_forgot_password_send_email));
    }

    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showProgressDialog(String title) {
        if (dialog != null) {
            dialog.setProgressTitle(title);
            dialog.show();
        }
    }

    public interface ForgotPasswordUiNavigationListener extends
            BackNavigationListener, KeyboardCommandListener {
        void showErrorScreen(int errorCode, String errorMsg);
    }

    private static class RequestLipService {

        private final WeakReference<Fragment> weakFragment;

        public RequestLipService(Fragment fragment) {
            weakFragment = new WeakReference<>(fragment);
        }

        public void requestForgotPassword(String email) {
            Logger.debug(ForgotPasswordFragment.TAG, "requestForgotPassword", "Requesting forgot password for : " + email);

            ForgotPasswordFragment fragment = (ForgotPasswordFragment) weakFragment.get();
            if (fragment != null && !fragment.isDetached()) {
                Activity activity = fragment.getActivity();
                if (activity != null) {
                    fragment.showProgressDialog(activity.getResources().getString(R.string.lip_sign_up_forgot_password_send_email));
                }
            }

            com.logitech.lip.account.AccountManager.forgotPassword(email, new ResponseListener<Object>() {
                @Override
                public void onSuccess(Object result) {
                    Logger.debug(ForgotPasswordFragment.TAG, "onSuccess", "Forgot password request successful");
                    ForgotPasswordFragment fragment = (ForgotPasswordFragment) weakFragment.get();
                    if (fragment != null && !fragment.isDetached()) {
                        fragment.dismissProgressDialog();
                        Activity activity = fragment.getActivity();
                        if (activity != null) {
                            fragment.uiNavigationListener.showPreviousScreen();
                            fragment.uiNavigationListener.showErrorScreen(0,
                                    activity.getString(R.string.lip_sign_up_forgot_password_email_sent_success));
                        }
                    }
                }

                @Override
                public void onError(ErrorCode errorCode, String errorMessage) {
                    Logger.debug(ForgotPasswordFragment.TAG, "onError", "errorCode : " + errorCode + " errorMessage :" + errorMessage);
                    ForgotPasswordFragment fragment = (ForgotPasswordFragment) weakFragment.get();
                    if (fragment != null && !fragment.isDetached()) {
                        fragment.dismissProgressDialog();
                        Activity activity = fragment.getActivity();
                        if (activity != null) {
                            fragment.uiNavigationListener.showErrorScreen(errorCode.ordinal(), errorMessage);
                        }
                    }
                }
            });
        }
    }
}

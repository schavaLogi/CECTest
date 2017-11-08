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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.logitech.lip.Logger;
import com.logitech.lip.R;
import com.logitech.lip.account.AccountManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.common.ProgressDialog;
import com.logitech.lip.ui.login.BaseFragment;
import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.ui.login.UserLoginInfoListener;

import java.lang.ref.WeakReference;


public class EmailVerifyFragment extends BaseFragment {

    public static final String TAG = EmailVerifyFragment.class.getSimpleName();

    private Handler pollHandler;
    private ProgressDialog dialog;

    private UserLoginInfoListener userLoginInfoListener;

    public static EmailVerifyFragment newInstance() {
        return new EmailVerifyFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            userLoginInfoListener = (UserLoginInfoListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement UserLoginInfoListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lip_frag_verify_email, container, false);
        initUiControls(rootView);

        initTitleBar(rootView);

        pollHandler = new EmailVerifyPollHandler(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Remove Existed message and put new message to process
        pollHandler.removeMessages(EmailVerifyPollHandler.POLL_EMAIL_STATUS);
        // The handler waits for 5 seconds before resuming the polling 
        pollHandler.sendEmptyMessageDelayed(EmailVerifyPollHandler.POLL_EMAIL_STATUS, 5000);
    }

    @Override
    public void onStop() {
        pollHandler.removeMessages(EmailVerifyPollHandler.POLL_EMAIL_STATUS);
		pollHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    private void initTitleBar(View rootView) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        titleBar.setTitle(getString(R.string.lip_sign_up_confirm_email))
                .setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EmailVerifyFragment.this.getActivity().onBackPressed();
                    }
                });
    }

    private void initUiControls(View rootView) {

        LoginSelectorActivity activity = (LoginSelectorActivity) getActivity();
        String email = activity.getCurrentUserInfo().getEmail();

        Button resendBtn = (Button) rootView.findViewById(R.id.sign_up_resend_email);

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollHandler.sendEmptyMessage(EmailVerifyPollHandler.RESEND_VERIFICATION_EMAIL);
            }
        });

        Button verifyLater = (Button) rootView.findViewById(R.id.sign_up_verify_later);
        verifyLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollHandler.removeMessages(EmailVerifyPollHandler.POLL_EMAIL_STATUS);
                userLoginInfoListener.userLoginComplete(true);
            }
        });

        TextView mailTextView = (TextView) rootView.findViewById(R.id.sign_up_verify_email);
        mailTextView.setText(email);

        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
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

    private static class EmailVerifyPollHandler extends Handler {
        static final int POLL_EMAIL_STATUS = 2000;
        static final int RESEND_VERIFICATION_EMAIL = 2001;
        static final long POLL_TIME = 10000;

        private final WeakReference<Fragment> weakFragment;

        EmailVerifyPollHandler(Fragment fragment) {
            super(Looper.getMainLooper());
            weakFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case POLL_EMAIL_STATUS: {
                    AccountManager.getCurrentAccountToken(false, new ResponseListener<AccountToken>() {
                        @Override
                        public void onSuccess(AccountToken result) {
                            new RequestLipService(weakFragment.get()).requestUserInfo(result.getAccessToken());
                        }

                        @Override
                        public void onError(ErrorCode errorCode, String errorMessage) {
                            Logger.debug("EmailVerifyPollHandler", "handleMessage",
                                    "POLL_EMAIL_STATUS =" + errorMessage);
                        }
                    });
                }
                break;
                case RESEND_VERIFICATION_EMAIL:
                    AccountManager.getCurrentAccountToken(false, new ResponseListener<AccountToken>() {
                        @Override
                        public void onSuccess(AccountToken result) {
                            new RequestLipService(weakFragment.get()).requestResendMail(result.getAccessToken());
                        }

                        @Override
                        public void onError(ErrorCode errorCode, String errorMessage) {
                            Logger.debug("EmailVerifyPollHandler", "handleMessage",
                                    "RESEND_VERIFICATION_EMAIL =" + errorMessage);
                        }
                    });

                    break;
                default:
                    break;
            }
        }
    }

    private static class RequestLipService extends ResponseListener<UserInfo> {
        private final WeakReference<Fragment> weakFragment;

        RequestLipService(Fragment fragment) {
            weakFragment = new WeakReference<>(fragment);
        }

        void requestUserInfo(String accessToken) {
            Logger.info(TAG, "RequestLipService", "requestUserInfo");
            AccountManager.getUserInfo(accessToken, this);
        }

        void requestResendMail(String accessToken) {
            Logger.info(TAG, "RequestLipService", "requestUserInfo");
            EmailVerifyFragment fragment = (EmailVerifyFragment) weakFragment.get();
            if (fragment != null && !fragment.isDetached()) {
                fragment.showProgressDialog(fragment.getActivity().getString(R.string.lip_sign_up_forgot_password_send_email));
            }
            AccountManager.resendVerificationMail(accessToken, new ResponseListener<Object>() {
                @Override
                public void onSuccess(Object result) {
                    EmailVerifyFragment fragment = (EmailVerifyFragment) weakFragment.get();
                    if (fragment != null && !fragment.isDetached()) {
                        fragment.dismissProgressDialog();
                    }
                }

                @Override
                public void onError(ErrorCode errorCode, String errorMessage) {
                    EmailVerifyFragment fragment = (EmailVerifyFragment) weakFragment.get();
                    if (fragment != null && !fragment.isDetached()) {
                        fragment.dismissProgressDialog();
                    }
                }
            });
        }

        @Override
        public void onSuccess(UserInfo result) {
            EmailVerifyFragment fragment = (EmailVerifyFragment) weakFragment.get();
            if (fragment != null) {
                if (!fragment.isDetached()) {
                    if (result != null && (result.isEmailVerified() || result.isEmailStatus())) {
                        fragment.userLoginInfoListener.userLoginComplete(true);
                    } else {
                        fragment.pollHandler.sendEmptyMessageDelayed(EmailVerifyPollHandler.POLL_EMAIL_STATUS,
                                EmailVerifyPollHandler.POLL_TIME);
                    }
                } else {
                    fragment.pollHandler.removeMessages(EmailVerifyPollHandler.POLL_EMAIL_STATUS);
                }
            }
        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            EmailVerifyFragment fragment = (EmailVerifyFragment) weakFragment.get();
            if (fragment != null) {
                fragment.pollHandler.sendEmptyMessageDelayed(EmailVerifyPollHandler.POLL_EMAIL_STATUS,
                        EmailVerifyPollHandler.POLL_TIME);
            }
        }
    }
}

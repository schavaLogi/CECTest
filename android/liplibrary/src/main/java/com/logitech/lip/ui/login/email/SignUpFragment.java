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
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.common.CustomToast;
import com.logitech.lip.ui.login.BackNavigationListener;
import com.logitech.lip.ui.login.BaseFragment;
import com.logitech.lip.ui.login.KeyboardCommandListener;
import com.logitech.lip.ui.login.UserLoginInfoListener;


public class SignUpFragment extends BaseFragment implements
        TextView.OnEditorActionListener, View.OnClickListener {

    private static final String TAG = SignUpFragment.class.getSimpleName();

    private static final int VALIDATE_SUCCESS = 1000;
    private static final int VALIDATE_WRONG_EMAIL = 1001;
    private static final int VALIDATE_PASSWORD_MINCOUNT = 1002;
    private static final int VALIDATE_PASSWORD_MAXCOUNT = 1003;
    private static final int VALIDATE_TERMS_CONDITIONS = 1004;
    private static final int VALIDATE_FIRST_NAME = 1005;
    private static final int VALIDATE_LAST_NAME = 1006;

    private static final int PASSWORD_MAX_LEN = 100;
    private static final int PASSWORD_MIN_LEN = 8;

    private AutoCompleteTextView firstNameView;
    private AutoCompleteTextView lastNameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private ImageView showPasswordImage;

    private UserInfo userInfo;

    private Activity parentActivity;

    /** Variable is declared globally to enable UI testing using Espresso */
    private int errResId;
    private LoginOptions loginOptions;
    private SignUpUiNavigationListener uiNavigationListener;

    public static SignUpFragment newInstance(LoginOptions loginOptions) {
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.setUserData(loginOptions);
        return signUpFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            uiNavigationListener = (SignUpUiNavigationListener) getActivity();
            parentActivity = getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement SignUpUiNavigationListener");
        }

        userInfo = new UserInfo(null, null, true);

        if (loginOptions != null) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lip_frag_sign_up, container, false);
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
    public void onStop() {
        super.onStop();
        dismissCustomToast();
    }

    private void setUserData(LoginOptions loginOptions) {
        this.loginOptions = loginOptions;
    }

    private void initTitleBar(View rootView) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        titleBar.setTitle(getString(R.string.lip_sign_up_create_account))
                .setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uiNavigationListener.showPreviousScreen();
                    }
                });
    }

    private void initUiControls(View rootView) {
        firstNameView = (AutoCompleteTextView) rootView.findViewById(R.id.sign_up_firstName);
        lastNameView = (AutoCompleteTextView) rootView.findViewById(R.id.sign_up_lastName);
        emailView = (AutoCompleteTextView) rootView.findViewById(R.id.sign_up_email);
        passwordView = (EditText) rootView.findViewById(R.id.sign_up_password);
        /* While setting the input type Android internally sets the font-family of the view.
         * Hence the typeface of email view is obtained and set to password view. */
        passwordView.setTypeface(emailView.getTypeface());

        Button createButton = (Button) rootView.findViewById(R.id.sign_up_createButton);

        if (createButton != null) {
            createButton.setOnClickListener(this);
        }
        //Need to validate all controls because Application can override the xml with few UI controls
        if (firstNameView != null) {
            firstNameView.setAdapter(null);
            firstNameView.setOnEditorActionListener(this);
            firstNameView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        }
        if (lastNameView != null) {
            lastNameView.setAdapter(null);
            lastNameView.setOnEditorActionListener(this);
            lastNameView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        }
        if (emailView != null) {
            emailView.setAdapter(null);
            emailView.setOnEditorActionListener(this);
        }
        if (passwordView != null) {
            if(isAutoInstaller()) {
                rootView.findViewById(R.id.passwordContainer).setVisibility(View.GONE);
            } else {
                passwordView.setOnEditorActionListener(this);
            }
        }

        // Open Keyboard only when there is no data in fields
        if(!(TextUtils.isEmpty(userInfo.getEmail()) & TextUtils.isEmpty(userInfo.getFirstName()) &
                TextUtils.isEmpty(userInfo.getLastName()))) {
            emailView.requestFocus();
        } else {
            uiNavigationListener.showKeyboard(emailView);
        }

        TextView haveAccount = (TextView) rootView.findViewById(R.id.sign_up_already_account);
        if (haveAccount != null) {
            if(isAutoInstaller()) {
                haveAccount.setVisibility(View.INVISIBLE);
            } else {
                haveAccount.setOnClickListener(this);
            }
        }
        RelativeLayout showPasswordContainer = (RelativeLayout)rootView.findViewById(R.id.sign_up_show_password_container);
        showPasswordImage = (ImageView) rootView.findViewById(R.id.sign_up_show_password);
        if (showPasswordImage != null) {
            Object objTag = showPasswordImage.getTag();
            if (objTag != null) {
                boolean isChecked = (boolean) objTag;
                showPassword(showPasswordImage, isChecked);
            } else {
                showPasswordImage.setTag(false);
            }
            showPasswordContainer.setOnClickListener(this);
        }
    }

    private void launchSignInFragment() {
        uiNavigationListener.showSignInScreen();
        Logger.debug(TAG, "launchSignInFragment","Launching Sign in fragment");
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            validateAndContinue();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_up_createButton) {
            validateAndContinue();
            uiNavigationListener.hideKeyboard();
            Logger.info(TAG,"onClick", "Create account button clicked");
        } else if (v.getId() == R.id.sign_up_already_account) {
            uiNavigationListener.showPreviousScreen();
            launchSignInFragment();
        } else if (v.getId() == R.id.sign_up_show_password_container) {
            boolean isChecked = (boolean) showPasswordImage.getTag();
            showPassword(showPasswordImage, !isChecked);
            showPasswordImage.setTag(!isChecked);
        }

    }

    private int validateUserInfo() {
        if (emailView != null) {
            String emailTxt = emailView.getText().toString().trim();
            if (!emailTxt.matches(Patterns.EMAIL_ADDRESS.pattern())) {
                return VALIDATE_WRONG_EMAIL;
            }
            userInfo.setEmail(emailTxt);
        }

        if (passwordView != null && !isAutoInstaller()) {
            String password = passwordView.getText().toString();
            // Check Minimum Length
            if (TextUtils.isEmpty(password) || password.length() < PASSWORD_MIN_LEN)
                return VALIDATE_PASSWORD_MINCOUNT;
            else if (password.length() > PASSWORD_MAX_LEN)
                return VALIDATE_PASSWORD_MAXCOUNT;
            userInfo.setPassword(password);

        }
        if (firstNameView != null) {
            String firstName = firstNameView.getText().toString().trim();
            if (TextUtils.isEmpty(firstName))
                return VALIDATE_FIRST_NAME;
            userInfo.setFirstName(firstName);
        }
        if (lastNameView != null) {
            String lastName = lastNameView.getText().toString().trim();
            if (TextUtils.isEmpty(lastName))
                return VALIDATE_LAST_NAME;
            userInfo.setLastName(lastName);
        }
        return VALIDATE_SUCCESS;
    }

    private void updateUserInfo() {
        if (emailView != null) {
            String emailTxt = emailView.getText().toString().trim();
            userInfo.setEmail(emailTxt);
        }
        if (passwordView != null && !isAutoInstaller()) {
            String password = passwordView.getText().toString();
            userInfo.setPassword(password);
        }
        if (firstNameView != null) {
            String firstName = firstNameView.getText().toString();
            userInfo.setFirstName(firstName);
        }
        if (lastNameView != null) {
            String lastName = lastNameView.getText().toString();
            userInfo.setLastName(lastName);
        }
        userInfo.setCreate(true);
        userInfo.setVerifyEmail(LIPSdk.isVerifyEmail());
    }

    /** This method is used to get the error id for UI testing using Espresso*/
    public int getErrResId(){
        return errResId;
    }

    private void validateAndContinue() {
        updateUserInfo();
        int validationResult = validateUserInfo();
        if (validationResult == VALIDATE_SUCCESS) {
            uiNavigationListener.showKeyboard(emailView);
            uiNavigationListener.showTouOptInScreen(userInfo);
        } else {
            errResId = R.string.lip_sign_up_error_toast_accept_conditions;
            if (validationResult == VALIDATE_WRONG_EMAIL) {
                errResId = R.string.lip_sign_up_error_toast_invalid_email;
            } else if (validationResult == VALIDATE_PASSWORD_MINCOUNT) {
                errResId = R.string.lip_sign_up_error_toast_min_count_pwd;
            } else if (validationResult == VALIDATE_PASSWORD_MAXCOUNT) {
                errResId = R.string.lip_sign_up_error_toast_max_count_pwd;
            } else if (validationResult == VALIDATE_TERMS_CONDITIONS) {
                errResId = R.string.lip_sign_up_error_toast_accept_conditions;
            } else if(validationResult == VALIDATE_FIRST_NAME) {
                errResId = R.string.lip_sign_up_error_toast_first_name_missing;
            }else if(validationResult == VALIDATE_LAST_NAME) {
                errResId = R.string.lip_sign_up_error_toast_last_name_missing;
            }
            showCustomToast(getString(errResId));
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

    private boolean isAutoInstaller() {
        if(loginOptions != null && loginOptions.isAutoInstaller()) {
            return true;
        }
        return false;
    }

    public interface SignUpUiNavigationListener extends KeyboardCommandListener,
            BackNavigationListener, UserLoginInfoListener {
        void showSignInScreen();
        void showTouOptInScreen(UserInfo userInfo);
    }
}

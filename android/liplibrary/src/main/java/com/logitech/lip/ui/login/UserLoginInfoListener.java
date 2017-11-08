package com.logitech.lip.ui.login;

import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.SocialIdentity;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.IListener;
import com.logitech.lip.profile.SocialProfile;

public interface UserLoginInfoListener {
    /**
     * Used to update the login response along with Tou & Opt-In
     * @param token Lip server response
     * @param touAccepted
     * @param keepMeUpdate
     */
    void updateLoginResponse(AccountToken token, boolean touAccepted, boolean keepMeUpdate);

    /**
     * Used to update login error info
     * @param errorCode
     * @param errorMsg
     */
    void updateLoginResponse(IListener.ErrorCode errorCode, String errorMsg);

    /**
     * Update current user info with entered details in SignIn or SignUp screen
     * @param userInfo
     */
    void updateCurrentUserInfo(UserInfo userInfo);
    /**
     * Update current user info with entered details in SignIn
     * @param email
     * @param password
     */
    void updateCurrentUserInfo(String email, String password);
    /**
     * Update current user info social login token details
     * @param identity
     */
    void updateCurrentUserInfo(SocialIdentity identity);
    /**
     * Update current user info social profile
     * @param socialProfile
     */
    void updateCurrentUserInfo(SocialProfile socialProfile);

    /**
     * Update current user login sucess or failure.
     * As we hold tokens while navigating to Email verification or password reset scree
     * @param isSuccess   True - Indicates login success
     *                    False - Indicated login failure
     */
    void userLoginComplete(boolean isSuccess);

    /**
     * Return current user login details (We can get UserInfo from idToken)
     * @return
     */
    AccountToken getCurrentAccountToken();
}

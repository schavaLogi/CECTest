package com.cectest.lipmodule;

import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.UserInfo;

public class LoginResponse {

    private UserInfo userInfo;
    private AccountToken accountToken;

    private String errorCode;
    private String errorMessage;

    public LoginResponse(UserInfo userInfo, AccountToken accountToken) {
        this.userInfo = userInfo;
        this.accountToken = accountToken;
    }

    public LoginResponse(String errorCode, String errorMessage,
            UserInfo userInfo) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public AccountToken getAccountToken() {
        return accountToken;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

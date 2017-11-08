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
package com.logitech.lip;

import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.UserInfo;

/**
 * Interface to delegate the Login Status
 */
public interface LoginListener {
    /**
     * This method is called when the login is successful
     * @param userInfo UserInfo
     * @param token AccountToken
     */
    void onLoginSuccess(UserInfo userInfo, AccountToken token);

    /**
     * This method is called when the login is not successful
     * @param userInfo UserInfo
     * @param errorCode errorCode
     * @param errorMessage errorMessage
     */
    void onLoginError(UserInfo userInfo, int errorCode, String errorMessage);
    // TODO define error codes
}

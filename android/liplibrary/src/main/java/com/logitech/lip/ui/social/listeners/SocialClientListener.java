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

package com.logitech.lip.ui.social.listeners;


public interface SocialClientListener {

    int SUCCESS = 0;
    int SERVICE_MISSING = 1;
    int SERVICE_VERSION_UPDATE_REQUIRED = 2;
    int SERVICE_DISABLED = 3;
    int SIGN_IN_REQUIRED = 4;
    int INVALID_ACCOUNT = 5;
    int RESOLUTION_REQUIRED = 6;
    int NETWORK_ERROR = 7;
    int INTERNAL_ERROR = 8;
    int SERVICE_INVALID = 9;
    int DEVELOPER_ERROR = 10;
    int LICENSE_CHECK_FAILED = 11;
    int CANCELED = 13;
    int TIMEOUT = 14;
    int INTERRUPTED = 15;
    int API_UNAVAILABLE = 16;
    int SIGN_IN_FAILED = 17;
    int SERVICE_UPDATING = 18;
    int SERVICE_MISSING_PERMISSION = 19;

    void onSocialLoginError(int errorCode, String errorMessage);
}

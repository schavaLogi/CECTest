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

import com.logitech.lip.account.model.SocialIdentity;


public interface SocialClientLoginListener extends SocialClientListener {
    void onSocialLoginSuccess(SocialIdentity identity);
}

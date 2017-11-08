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
package com.logitech.lip.ui.social;

import android.content.Intent;

import com.logitech.lip.profile.SocialProfile;
import com.logitech.lip.ui.SocialClient;

final class NullClient extends SocialClient {
    @Override
    public void initialize() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void requestLogout() {

    }

    public SocialProfile getProfile() {
        return null;
    }
}

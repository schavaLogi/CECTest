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
package com.logitech.lip.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.logitech.lip.ui.SocialClient;
import com.logitech.lip.ui.social.SocialClientFactory;
import com.logitech.lip.ui.social.listeners.SocialClientLoginListener;

public final class SocialLoginController {

    private static final String TAG = SocialLoginController.class.getSimpleName();

    private SocialClient currentClient;
    private String currentProvider;

    public SocialLoginController(){
        SocialClientFactory clientFactory = SocialClientFactory.getInstance();
        currentClient = clientFactory.getClient(SocialClient.Provider.PROVIDER_NONE);
    }

    public void onStart() {
        currentClient.onStart();
    }
    public void onStop() {
        currentClient.onStop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentClient.onActivityResult(requestCode, resultCode, data);
    }

    public SocialClient getSelectedClient() {
        return currentClient;
    }

    public void requestLogin(Activity activity, String provider, SocialClientLoginListener listener) {
        if(provider != null && !provider.equals(currentProvider)) {

            currentClient = SocialClientFactory.getInstance().getClient(provider);
            // Check connection and try
            currentClient.requestLogin(activity, listener);
        }
        // TODO Handle Error Scenarios
    }

    public void requestLogout() {
        currentClient.requestLogout();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        currentClient.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

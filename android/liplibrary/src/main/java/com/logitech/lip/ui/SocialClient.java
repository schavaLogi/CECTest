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

package com.logitech.lip.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.logitech.lip.Logger;
import com.logitech.lip.profile.SocialProfile;
import com.logitech.lip.ui.social.listeners.SocialClientListener;
import com.logitech.lip.ui.social.listeners.SocialClientLoginListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public abstract class SocialClient {
    private static final String TAG = SocialClient.class.getSimpleName();

    public interface Provider {
        String PROVIDER_GOOGLE = "google";
        String PROVIDER_FACEBOOK = "facebook";
        String PROVIDER_NONE = "none";
    }
    // Request Types to make it as unique
    protected static final String REQUEST_LOGIN = "REQUEST_LOGIN";

    protected final Map<String, SocialClientListener> localListeners =
            new HashMap<>();

    private WeakReference<Activity> refActivity;

    protected SocialClient() {

    }

    public abstract void initialize();

    //UI Callback API's
    public void onStart() {
    }

    public void onStop() {
    }

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

    }

    public Activity getActivity(){
        if(refActivity != null) {
            return refActivity.get();
        }
        return null;
    }

    /**
     * Will register listener to update stats . Must be called all child classes when overridden
     * @param activity login Activity
     * @param listener Social login Listener
     */
    public void requestLogin(Activity activity, SocialClientLoginListener listener){
        Logger.info(TAG, "requestLogin", "requestLogin");
        refActivity = new WeakReference<>(activity);
        registerListener(REQUEST_LOGIN, listener);
    }

    public abstract void requestLogout();

    private void registerListener(String listenerID, SocialClientListener socialClientListener) {
        if (socialClientListener != null) {
            localListeners.put(listenerID, socialClientListener);
            Logger.info(TAG, "registerListener", "Listener registered successfully");
        }
    }

    public abstract SocialProfile getProfile();
}

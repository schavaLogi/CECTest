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

package com.logitech.lip.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.account.model.AccountToken;

/**
 * This class can be extended to receive notifications of account token changes The {@link
 * #stopTracking()} method should be called in the onDestroy() method of the receiving Activity or
 * Fragment.
 */
public abstract class AccountTokenTracker {

    public static final String ACTION_CURRENT_ACCOUNT_TOKEN_CHANGED =
            "com.logitech.lipsdk.ACTION_CURRENT_ACCOUNT_TOKEN_CHANGED";

    /*key to retrieve access token info in intent bundle */
    static final String EXTRA_ACCOUNT_ACCESS_TOKEN =
            "com.logitech.lipsdk.EXTRA_ACCOUNT_TOKEN";

    private final BroadcastReceiver receiver;
    private final LocalBroadcastManager broadcastManager;
    private boolean isTracking = false;

    protected abstract void onAccountTokenChanged(AccountToken accountToken);

    public AccountTokenTracker() {

        LIPSdk.isInitialized();

        this.receiver = new CurrentAccessTokenBroadcastReceiver();
        this.broadcastManager = LocalBroadcastManager.getInstance(
                LIPSdk.getContext());

        // startTracking();
    }

    /**
     * Starts tracking the current access token
     */
    public void startTracking() {
        if (isTracking) {
            return;
        }

        addBroadcastReceiver();

        isTracking = true;
    }

    /**
     * Stops tracking the current Account token.
     */
    public void stopTracking() {
        if (!isTracking) {
            return;
        }
        broadcastManager.unregisterReceiver(receiver);
        isTracking = false;
    }

    public boolean isTracking() {
        return isTracking;
    }

    private class CurrentAccessTokenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CURRENT_ACCOUNT_TOKEN_CHANGED.equals(intent.getAction())) {
                AccountToken token = intent.getParcelableExtra(AccountTokenTracker.EXTRA_ACCOUNT_ACCESS_TOKEN);
                onAccountTokenChanged(token);
            }
        }
    }

    private void addBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CURRENT_ACCOUNT_TOKEN_CHANGED);
        broadcastManager.registerReceiver(receiver, filter);
    }

}

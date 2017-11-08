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

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.SecurePrefManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.SignInResponse;
import com.logitech.lip.account.model.SocialIdentity;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.IListener;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.utility.JWTokenDecoder;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is responsible to handle the Account token by tracking changes in tokens
 * Handles storing of Account token , refresh when it expired and notify the token changes through broadcast receiver
 */
final class AccountTokenManager implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = AccountTokenManager.class.getSimpleName();

    /* Preference key for the account token*/
    private static final String ACCOUNT_TOKEN_KEY = "ACCOUNT_TOKEN_KEY";

    /* Preference key for the Refresh token*/
    public static final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY";

    /* Preference key for the Social account token*/
    private static final String SOCIAL_ACCOUNT_TOKEN = "SOCIAL_ACCOUNT_TOKEN";

    /* Preference key for the Social Profile*/
    private static final String SOCIAL_PROFILE_INFO = "SOCIAL_PROFILE_INFO";

    /* Preference key for the Social Identity*/
    public static final String SOCIAL_IDENTITY_TOKEN = "SOCIAL_IDENTITY_TOKENS";

    public static final String ACTIVE_IDENTITY = "ACTIVE_IDENTITY";

    private static final int TOKEN_THRESHOLD_MILLI_SECONDS = 5 * 60 * 1000;

    private static AccountTokenManager INSTANCE;

    private final LocalBroadcastManager localBroadcastManager;

    private AccountToken currentAccountToken;
    private final SecurePrefManager securePref;
    private String activeIdentity;
    private int resumed;
    private int stopped;

    // To indicate token refresh triggered
    private boolean tokenReqInProgress = false;

    // Hold Refresh Token Listeners to respond back to clients
    private List<ResponseListener<AccountToken>> responseListeners = new ArrayList<>();

    private AccountTokenManager(LocalBroadcastManager localBroadcastManager) {
        this.localBroadcastManager = localBroadcastManager;
        securePref = new SecurePrefManager(LIPSdk.getContext());
    }

    private void listenActivityLifecycleCallbacks() {
        // Register Callbacks with delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LIPSdk.getApplication().registerActivityLifecycleCallbacks(INSTANCE);
                Logger.debug(TAG, "listenActivityLifecycleCallbacks", "Registered callbacks");
            }
        }, 100);
    }

    public static AccountTokenManager getInstance() {
        LIPSdk.isInitialized();
        if (INSTANCE == null) {
            synchronized (AccountTokenManager.class) {
                if (INSTANCE == null) {
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(
                            LIPSdk.getContext());
                    INSTANCE = new AccountTokenManager(localBroadcastManager);
                    // INSTANCE.listenActivityLifecycleCallbacks();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * This API must be called when there is a change in LipSdk Configuration
     */
    protected void updateActiveIdentity() {
        currentAccountToken = null;
        activeIdentity = LIPSdk.getConfiguration().getActiveIdentity();
        if(TextUtils.isEmpty(activeIdentity)) {
            activeIdentity = securePref.getData(ACTIVE_IDENTITY, null , false);
        } else {
            securePref.saveData(ACTIVE_IDENTITY , activeIdentity, false);
        }
    }

    protected String getActiveIdentity() {
        if(activeIdentity == null) {
            activeIdentity = securePref.getData(ACTIVE_IDENTITY, null, false);
        }
        return activeIdentity;
    }

    public void setCurrentAccountToken(SignInResponse response, UserInfo userInfo) {
        // In case Application not known about identity make it current login as active
        String identity = LIPSdk.getConfiguration().getActiveIdentity();
        if(TextUtils.isEmpty(identity) && userInfo != null && response != null) {
            activeIdentity = userInfo.getEmail();
            securePref.saveData(ACTIVE_IDENTITY , activeIdentity, false);
        }
        Logger.debug(TAG,"setCurrentAccountToken", "activeIdentity =" + activeIdentity);

        if(response != null) {
            AccountToken oldAccountToken = currentAccountToken;
            currentAccountToken = response.getAccountToken();

            // Decode ID Token to get Email Id and set tokens
            String emailToken = activeIdentity;
            UserInfo user = JWTokenDecoder.getUserInfo(currentAccountToken.getIdToken());
            if(user != null && user.getEmail() != null) {
                emailToken = user.getEmail();
            }


            // Save Social Tokens for passing along with Account tokens
            securePref.clearKey(SOCIAL_IDENTITY_TOKEN + emailToken);
            if(userInfo != null && userInfo.getSocial() != null) {
                currentAccountToken.setSocial(userInfo.getSocial());

                securePref.saveData(SOCIAL_IDENTITY_TOKEN + emailToken,
                        new Gson().toJson(userInfo.getSocial(), SocialIdentity.class), true);
            }

            // Save Account Token in JSON format
            securePref.saveData(ACCOUNT_TOKEN_KEY + emailToken,
                    new Gson().toJson(currentAccountToken, AccountToken.class), true);

            securePref.saveData(REFRESH_TOKEN_KEY + emailToken,
                    response.getRefreshToken(), true);

            // Compare old Token and new token if Different send Broadcast Message
            if (currentAccountToken != null && !currentAccountToken.equals(oldAccountToken)) {
                sendCurrentAccountTokenChangedBroadcast(currentAccountToken);
            }
        } else {
            currentAccountToken = null;

            securePref.clearKey(ACCOUNT_TOKEN_KEY + activeIdentity);
            securePref.clearKey(REFRESH_TOKEN_KEY + activeIdentity);
            securePref.clearKey(SOCIAL_IDENTITY_TOKEN + activeIdentity);
        }
    }

    public void getCurrentAccountToken(boolean doRefresh, final ResponseListener<AccountToken> listener) {
        AccountToken accountToken = currentAccountToken;

        Logger.debug(TAG, "getCurrentAccountToken","doRefresh =" + doRefresh + "activeIdentity =" + activeIdentity);

        if(currentAccountToken == null) {
            String token = securePref.getData(ACCOUNT_TOKEN_KEY + activeIdentity, null, true);
            if(token != null) {
                try {
                    accountToken = new Gson().fromJson(token, AccountToken.class);
                } catch (JsonSyntaxException e) {

                }
            }
        }
        if(accountToken == null || activeIdentity == null) {
            if(listener != null) {
                listener.onError(IListener.ErrorCode.ERROR_CODE_INTERNAL, "There is no token for activeIdentity");
            }
        } else {
            refreshAccountToken(doRefresh, accountToken, listener);
        }
    }

    public AccountToken getCurrentAccountToken() {
        AccountToken accountToken = currentAccountToken;
        Logger.debug(TAG, "getCurrentAccountToken","activeIdentity =" + activeIdentity);

        if(accountToken == null) {
            String token = securePref.getData(ACCOUNT_TOKEN_KEY + activeIdentity, null, true);
            if(token != null) {
                try {
                    accountToken = new Gson().fromJson(token, AccountToken.class);
                } catch (JsonSyntaxException e) {

                }
            }
        }

        if(activeIdentity == null || accountToken == null) {
            return null;
        }
        // Refresh Account token silently if needed
        refreshAccountToken(false, accountToken, null);

        return accountToken;
    }

    private void sendCurrentAccountTokenChangedBroadcast(AccountToken currentAccountToken) {

        Intent intent = new Intent(AccountTokenTracker.ACTION_CURRENT_ACCOUNT_TOKEN_CHANGED);

        AccountToken token = new AccountToken(currentAccountToken.getIdToken(),
                currentAccountToken.getAccessToken(),
                currentAccountToken.getExpireTime(), currentAccountToken.isEmailVerified(),
                currentAccountToken.isPasswordReset());

        intent.putExtra(AccountTokenTracker.EXTRA_ACCOUNT_ACCESS_TOKEN, token);


        localBroadcastManager.sendBroadcast(intent);
    }

    private boolean shouldRequestNewAccessToken(AccountToken accountToken) {
        if (accountToken != null) {
            long expireTime = accountToken.getExpireTime();
            long currentTime = System.currentTimeMillis() + TOKEN_THRESHOLD_MILLI_SECONDS;
            return currentTime >= expireTime;
        }
        return true;
    }

    /**
     * Will read Account token and expire time and will refresh if required
     * @param doRefresh    refresh always
     * @param accountToken accountToken
     * @param listener     callback listener
     */
    private void refreshAccountToken(boolean doRefresh, final AccountToken accountToken, final ResponseListener<AccountToken> listener) {

        // Check we need to refresh the Account token or not
        InternalRefreshResponseListener internalRefresher =
                new InternalRefreshResponseListener(accountToken, listener);

        if (accountToken != null) {
            if (tokenReqInProgress || doRefresh || shouldRequestNewAccessToken(accountToken)) {
                internalRefresher.refreshAccountToken();
            } else {
                if (listener != null) {
                    listener.onSuccess(accountToken);
                }
            }
        } else {
            internalRefresher.refreshAccountToken();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        if (resumed > stopped) {
            getCurrentAccountToken();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        if (resumed == stopped) {
            resumed = stopped = 0;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void notifyRefreshListeners(AccountToken result, IListener.ErrorCode errorCode,
            String errorMessage) {
        if (result != null) {
            for (ResponseListener<AccountToken> responseListener : responseListeners) {
                responseListener.onSuccess(result);
            }
        } else {
            for (ResponseListener<AccountToken> responseListener : responseListeners) {
                responseListener.onError(errorCode, errorMessage);
            }
        }
        responseListeners.clear();
    }
	
    private class InternalRefreshResponseListener extends ResponseListener<AccountToken> {

        private final ResponseListener<AccountToken> listener;
        private final AccountToken accountToken;

        public InternalRefreshResponseListener(final AccountToken accountToken,
                final ResponseListener<AccountToken> listener) {
            this.accountToken = accountToken;
            this.listener = listener;
        }

        public void refreshAccountToken() {
            if(tokenReqInProgress) {
                if(listener != null) {
                    responseListeners.add(listener);
                }
                Logger.info(TAG,"refreshAccountToken","Request already in progress");
                return;
            }
            tokenReqInProgress = true;
            AccountManager.refreshAccountToken(this);
        }

        @Override
        public void onSuccess(AccountToken result) {
            Logger.info(TAG, "refreshAccountToken", "onSuccess");
            tokenReqInProgress = false;
            if (listener != null) {
                listener.onSuccess(result);
            }
            notifyRefreshListeners(result, null, null);
        }

        @Override
        public void onError(ErrorCode errorCode, String errorMessage) {
            tokenReqInProgress = false;
            if(errorCode == IListener.ErrorCode.NETWORK_AUTHUNTICATION_ERROR ||
                    errorCode == IListener.ErrorCode.ERROR_NULL_REFRESH_TOKEN ||
                    errorCode == IListener.ErrorCode.SERVER_ERROR) {
                if (listener != null) {
                    listener.onError(errorCode, errorMessage);
                }
                notifyRefreshListeners(null, errorCode, errorMessage);
            } else if (listener != null && !shouldRequestNewAccessToken(accountToken)) {
                listener.onSuccess(accountToken);
                notifyRefreshListeners(accountToken, null, null);
            } else {
                if (listener != null) {
                    listener.onError(errorCode, errorMessage);
                }
                notifyRefreshListeners(null, errorCode, errorMessage);
            }
            Logger.debug(TAG,"refreshAccountToken", "onActivityResumed refresh Account token errorMessage="
                    + errorMessage);
        }
    }
}

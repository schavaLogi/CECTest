package com.cectest.lipmodule;


import com.cectest.lipmodule.converter.LoginResponseConverter;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.logitech.lip.LoginController;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.LoginListener;
import com.logitech.lip.account.AccountManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.ChangePassword;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.ResponseListener;

import java.util.HashMap;
import java.util.Map;

public class LipModule extends ReactContextBaseJavaModule {

    private Promise mLoginPromise;

    public LipModule(ReactApplicationContext reactContext) {
        super(reactContext);
        LoginController.getInstance().registerLoginListener(new LoginListener() {
            @Override
            public void onLoginSuccess(UserInfo userInfo, AccountToken token) {

                LoginResponse response = new LoginResponse(userInfo, token);
                WritableMap map = new LoginResponseConverter().toJSObject(response);

                if(mLoginPromise != null) {
                    mLoginPromise.resolve(map);
                }
                mLoginPromise = null;
            }

            @Override
            public void onLoginError(UserInfo userInfo, int errorCode, String errorMessage) {
                if(mLoginPromise != null) {
                    mLoginPromise.reject(Integer.toString(errorCode),errorMessage);
                }
                mLoginPromise = null;
            }
        });
    }

    @Override
    public String getName() {
        return "LipModule";
    }

    @Override public Map<String, Object> getConstants() {
        return new HashMap<>();
    }


    @ReactMethod
    public void requestLogin(ReadableMap loginOptions, Promise loginPromise) {
        LoginOptions.Builder builder = new LoginOptions.Builder();

        mLoginPromise = loginPromise;

        if(loginOptions != null) {
            if(loginOptions.hasKey("email")) {
                builder.setEmail(loginOptions.getString("email"));
            }
            if(loginOptions.hasKey("create")){
                builder.setIsCreate(loginOptions.getBoolean("create"));
            }

            if(loginOptions.hasKey("persistToken")) {
                builder.setIsPersistToken(loginOptions.getBoolean("persistToken"));
            }

            if(loginOptions.hasKey("validate")) {
                builder.setIsValidate(loginOptions.getBoolean("validate"));
            }

            if(loginOptions.hasKey("social")) {
                builder.setIsCreate(loginOptions.getBoolean("social"));
            }

            if(loginOptions.hasKey("autoInstaller")) {
                builder.setAutoInstaller(loginOptions.getBoolean("autoInstaller"));
            }
        }
        LoginController.getInstance().requestLogin(getCurrentActivity(), builder.build());
    }

    /*
    Note: ReactMethod's are not supporting overriding feature
    @ReactMethod
    public void requestLogin(boolean createMode, Promise loginPromise) {
        mLoginPromise = loginPromise;
        LoginController.getInstance().requestLogin(getCurrentActivity(), createMode);
    }

    @ReactMethod
    public void requestLogin(Promise loginPromise) {
        mLoginPromise = loginPromise;
        LoginController.getInstance().requestLogin(getCurrentActivity());
    }



	@ReactMethod
    public void requestLogin(final Promise promise) {
        mLoginPromise = promise;
        LoginController.getInstance().requestLogin(getCurrentActivity());
    }
	*/


    @ReactMethod
    public void getAccessToken(boolean forceRefresh, final Promise promise) {
        AccountManager.getCurrentAccountToken(forceRefresh, new ResponseListener<AccountToken>() {
            @Override
            public void onSuccess(AccountToken result) {
                WritableMap map = Arguments.createMap();
                if(result != null) {
                    map.putString("accessToken", result.getAccessToken());
                    map.putString("idToken", result.getIdToken());
                }
                promise.resolve(map);
            }

            @Override
            public void onError(ErrorCode errorCode, String errorMessage) {
                promise.reject(errorCode.toString(), errorMessage);
            }
        });
    }


    @ReactMethod
    public void requestSignOut(boolean allSessions) {
        LoginController.getInstance().requestLogout(allSessions);
    }

    @ReactMethod
    public void requestChangePassword(String accessToken, ReadableMap changeMap, final Promise promise) {
        if(changeMap != null) {
            String email = changeMap.getString("email");
            String newPassword = changeMap.getString("newPassword");
            String oldPassword = changeMap.getString("oldPassword");

            ChangePassword changePassword = new ChangePassword(email, oldPassword, newPassword);

            AccountManager.changePassword(accessToken, changePassword,
                    new ResponseListener<Object>() {
                        @Override
                        public void onSuccess(Object result) {
                            promise.resolve(result);
                        }
                        @Override
                        public void onError(ErrorCode errorCode, String errorMessage) {
                            promise.reject(errorCode.toString(), errorMessage);
                        }
                    });
        }

    }
}

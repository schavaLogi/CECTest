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

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.logitech.lip.account.AccountManager;
import com.logitech.lip.ui.common.TypefaceManager;
import com.logitech.lip.utility.KeyUtility;
import com.logitech.lip.volley.RequestQueue;
import com.logitech.lip.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * LIP SDK allows basic initialization and some customization of SDK behavior.
 */
public final class LIPSdk {

    private static final String TAG = LIPSdk.class.getSimpleName();

    private static Application application;

    private static boolean isInitialized;

    private static LipConfiguration configuration;

    private static AppConfiguration appConfiguration;

    /*To identify Keystore is ready and we can encrypt keys , If not fallback to alternate option*/
    private static boolean isKeyStoreReady = false;

    private static RequestQueue requestQueue;

    /**
     * Will be responsible to Initialize SDK and it's Sub modules
     * @param application Application
     * @param configuration Configuration
     * @throws RuntimeException In case application is not valid
     */
    public static void initialize(Application application, LipConfiguration configuration) {
        if (isInitialized) {
            return;
        }
        if (application != null && configuration != null) {
            isInitialized = true;
            LIPSdk.application = application;

            readConfigurations();

            validateConfiguration(configuration);

            Thread lazyLoad = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Create Keystore for RSA
                    isKeyStoreReady = KeyUtility.createNewKeys(getContext());
                    // Init Network Module
                    requestQueue = Volley.newRequestQueue(getContext());

                    // Pref Management based on API Level
                    checkAndUpdateAPILevel();
                }
            });
            lazyLoad.start();

        } else {
            throw new IllegalArgumentException("LipSdk exception application =" + application +
                    "Configuration =" + configuration);
        }
    }

    /**
     * Will be responsible to Update the configuration of the SDK and it's Sub modules
     * @param configuration Configuration
     * @throws RuntimeException In case application is not valid
     */
    public static void updateConfiguration(LipConfiguration configuration) {
        isInitialized();
        validateConfiguration(configuration);
    }

    /**
     * Returns context of the application
     * @return Context
     */
    public static Context getContext() {
        return application.getApplicationContext();
    }

    public static boolean isKeyStoreReady() {
        return isKeyStoreReady;
    }

    /**
     * Returns the application object
     * @return Application
     */
    public static Application getApplication() {
        return application;
    }

    /**
     * Returns the configuration of LIP
     * @return LipConfiguration
     */
    public static LipConfiguration getConfiguration() {
        return configuration;
    }

    public static boolean isVerifyEmail() {
        if(LIPSdk.getConfiguration() != null) {
            return LIPSdk.getConfiguration().isVerifyEmail();
        }
        return false;
    }

    /**
     * Returns the configuration of LIP
     * @return LipConfiguration
     */
    public static AppConfiguration getAppConfiguration() {
        return appConfiguration;
    }

    /**
     * Checks SDK is initialized or not (Used only for Validation)
     * @return true - if initialized, false - if not initialized
     * @throws RuntimeException If SDK not initialized
     */
    public static boolean isInitialized() {
        if (!isInitialized) {
            throw new IllegalArgumentException("LIPSdk Not yet initialized");
        }
        return isInitialized;
    }

    /**
     * Read SDK configuration from raw resource file and configure SDK features accordingly
     */
    private static void readConfigurations() {
        try {
            InputStream inputStream = LIPSdk.getContext().getResources().openRawResource(R.raw.liplibrary_config);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder out = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                String configData = out.toString();
                appConfiguration = new Gson().fromJson(configData, AppConfiguration.class);
            } finally {
                reader.close();
                inputStream.close();
            }
        } catch (IOException | JsonSyntaxException e) {
            appConfiguration = null;
        }
        // Initialize application specific configuration params
        if (appConfiguration != null) {
            TypefaceManager.initialize(appConfiguration.getCustomFont());
        } else {
            TypefaceManager.initialize(TypefaceManager.defaultFontSet);
        }
    }

    private static void validateConfiguration(LipConfiguration configuration) {
        if(configuration == null ||
                !isUrlValid(configuration.getServerUrl()) ||
                !isUrlValid(configuration.getTermsUseUrl()) ||
                !isUrlValid(configuration.getPrivacyPolicyUrl())){
            throw new IllegalArgumentException("LIP Configuration error " + "Url must be a valid");
        }
        LIPSdk.configuration = configuration;
        AccountManager.updateActiveIdentity();
    }


    private static boolean isUrlValid(String url) {
        if(!TextUtils.isEmpty(url) && URLUtil.isValidUrl(url)) {
            return true;
        }
        return false;
    }

    /*
     * Android API level version not matched then delete file.
     * Data is encrypted can be decrypted in same version
     */
    private static void checkAndUpdateAPILevel() {

        int nCurrentLevel = android.os.Build.VERSION.SDK_INT;

        SecurePrefManager secPref = new SecurePrefManager(getContext());

        String apiLevel = secPref.getData("ANDROID_API_LEVEL", "14", false);

        String currentApiLevel = String.valueOf(nCurrentLevel);

        if(!currentApiLevel.equalsIgnoreCase(apiLevel)) {
            secPref.clearAll();
        }
        secPref.saveData("ANDROID_API_LEVEL",
                String.valueOf(nCurrentLevel), false);
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

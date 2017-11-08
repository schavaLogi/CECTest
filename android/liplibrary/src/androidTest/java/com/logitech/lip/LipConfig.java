package com.logitech.lip;

/**
 * Created by nkumar3 on 7/18/2016.
 *
 * Class that contains the constants that are used in the Espresso test cases
 */

public class LipConfig {

    public static String SERVER_URL = "https://int-lip.dynamite.logi.com/";

    public static String SERVER_DUMMY_URL = "http://123.com";
    public static String PRIVACY_POLICY_URL = "http://123.com";
    public static String TOU_OPT_URL = "http://123.com";

    public static final String TEST_DUMMY_EMAIL = "abc@gmail.com";
    public static final String TEST_DUMMY_PASSWORD = "12345678";

    public static final String TEST_EMAIL = "hello@hello.com";

    public static LipConfiguration getConfiguration() {
        LipConfiguration configuration = new LipConfiguration.Builder()
                .setServerUrl(SERVER_DUMMY_URL)
                .setPrivacyPolicyUrl(PRIVACY_POLICY_URL)
                .setTermsUseUrl(TOU_OPT_URL)
                .setAnalyticCallback(null)
                .setIsVerifyEmail(false)
                .build();
        return configuration;
    }

    public static LoginOptions buildLoginOptions(boolean isCreateMode) {
        LoginOptions loginOptions = new LoginOptions.Builder()
                .setIsCreate(isCreateMode)
                .setIsPersistToken(true)
                .setIsValidate(true)
                .setEmail(LipConfig.TEST_DUMMY_EMAIL)
                .build();
        return loginOptions;
    }

    public static LoginOptions buildLoginOptions(boolean isCreateMode, boolean isValidate) {
        LoginOptions loginOptions = new LoginOptions.Builder()
                .setIsCreate(isCreateMode)
                .setIsPersistToken(true)
                .setIsValidate(isValidate)
                .setEmail(LipConfig.TEST_DUMMY_EMAIL)
                .build();
        return loginOptions;
    }

}

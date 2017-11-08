package com.logitech.lip;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LipSdkTest {
    private Activity activity;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> activityTestRule =
            new ActivityTestRule<>(LoginSelectorActivity.class);

    public static LipConfiguration getConfiguration() {
        LipConfiguration configuration = new LipConfiguration.Builder()
                .setServerUrl("http://123.com")
                .setPrivacyPolicyUrl("http://123.com")
                .setTermsUseUrl("http://123.com")
                .setAnalyticCallback(null)
                .setIsVerifyEmail(false)
                .build();
        return  configuration;
    }

    @Before
    public void setUp() throws Exception {
        activity = activityTestRule.getActivity();
        LIPSdk.initialize(activity.getApplication(), getConfiguration());
    }

    @After
    public void tearDown() throws Exception {
        activity.finish();
    }

    @Test
    public void testSDKInitialization(){
        assertTrue(LIPSdk.isInitialized());
    }

    @Test
    public void testAllPublicMethods() throws Exception {
        assertNotNull(LIPSdk.class.getMethod("initialize", Application.class, LipConfiguration.class));

        assertNotNull(LIPSdk.class.getMethod("isInitialized"));
        assertNotNull(LIPSdk.class.getMethod("getContext"));
        assertNotNull(LIPSdk.class.getMethod("getApplication"));
    }
}

package com.logitech.lip.acceptence.tests;

import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfig;
import com.logitech.lip.LipSdkTest;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.acceptence.screens.ErrorScreen;
import com.logitech.lip.acceptence.screens.ForgotPasswordScreen;
import com.logitech.lip.acceptence.screens.LoginSelectorScreen;
import com.logitech.lip.acceptence.screens.SignInScreen;
import com.logitech.lip.acceptence.tests.utils.TestUtils;
import com.logitech.lip.ui.VolleyIdlingResource;
import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestForgotPasswordFailScenario {

    @Rule
    public ActivityTestRule<LoginSelectorActivity> mActivityTestRule =
            new ActivityTestRule<LoginSelectorActivity>(LoginSelectorActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext =
                            InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent intent = new Intent(targetContext, LoginSelectorActivity.class);
                    LoginOptions loginOptions = LipConfig.buildLoginOptions(false);
                    intent.putExtra(LoginSelectorActivity.LOGIN_OPTIONS, loginOptions);
                    return intent;
                }
            };

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activity = mActivityTestRule.getActivity();
        if(LIPSdk.getConfiguration() != null) {
            LIPSdk.updateConfiguration(LipSdkTest.getConfiguration());
        }else {
            LIPSdk.initialize(mActivityTestRule.getActivity().getApplication(),
                    LipSdkTest.getConfiguration());
        }
        TestUtils.changeIdlePolicy();
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.unregisterAllIdlingResources();
        activity.finish();
    }

    @Test
    public void TestForgotPassword_FailScenario() {
        LoginSelectorScreen selectorScreen = new LoginSelectorScreen();
        selectorScreen.clickOnEmail();

        SignInScreen signInScreen = new SignInScreen();
        signInScreen.enterEmailAndPassword(LipConfig.TEST_DUMMY_EMAIL, LipConfig.TEST_DUMMY_PASSWORD);

        signInScreen.clickOnLogin();

        VolleyIdlingResource idlingResource = null;
        try {
            idlingResource = new VolleyIdlingResource("sign In call");
            registerIdlingResources(idlingResource);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        //Register for volley IR
        ErrorScreen errorScreen = new ErrorScreen();
        errorScreen.clickOnPositiveButton();

        //Unregister Volley IR
        unregisterIdlingResources(idlingResource);

        signInScreen = new SignInScreen();
        signInScreen.clickOnForgotPassword();

        try {
            idlingResource = new VolleyIdlingResource("Forgot Password send email call");
            registerIdlingResources(idlingResource);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        ForgotPasswordScreen forgotPasswordScreen = new ForgotPasswordScreen();
        forgotPasswordScreen.clickOnSendEmail();

        errorScreen = new ErrorScreen();
        errorScreen.clickOnPositiveButton();

        //Unregister Volley IR
        unregisterIdlingResources(idlingResource);
    }
}

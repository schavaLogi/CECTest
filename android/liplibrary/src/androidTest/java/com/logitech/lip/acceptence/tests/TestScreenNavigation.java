package com.logitech.lip.acceptence.tests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfig;
import com.logitech.lip.LipSdkTest;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.acceptence.screens.ForgotPasswordScreen;
import com.logitech.lip.acceptence.screens.LoginSelectorScreen;
import com.logitech.lip.acceptence.screens.SignInScreen;
import com.logitech.lip.acceptence.screens.SignUpScreen;
import com.logitech.lip.acceptence.tests.utils.TestUtils;
import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestScreenNavigation {
    @Rule
    public ActivityTestRule<LoginSelectorActivity> mActivityTestRule =
            new ActivityTestRule<LoginSelectorActivity>(LoginSelectorActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext =
                            InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent intent = new Intent(targetContext, LoginSelectorActivity.class);
                    LoginOptions loginOptions = LipConfig.buildLoginOptions(true);
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
    public void testSignUp_SignInNavigation() {
        LoginSelectorScreen selectorScreen = new LoginSelectorScreen();
        selectorScreen.clickOnEmail();

        SignUpScreen signUpScreen = new SignUpScreen();
        signUpScreen.clickOnAlreadyHaveAccount();

        SignInScreen signInScreen = new SignInScreen();
        signInScreen.enterEmailAndPassword(LipConfig.TEST_DUMMY_EMAIL, LipConfig.TEST_DUMMY_PASSWORD);
    }

    @Test
    public void testSignIn_SignUpNavigation() {
        testSignUp_SignInNavigation();

        SignInScreen signInScreen = new SignInScreen();
        signInScreen.enterEmailAndPassword(LipConfig.TEST_DUMMY_EMAIL, LipConfig.TEST_DUMMY_PASSWORD);
        signInScreen.clickOnNewAccount();

        SignUpScreen signUpScreen = new SignUpScreen();
        signUpScreen.enterSignUpDetails(LipConfig.TEST_DUMMY_EMAIL, LipConfig.TEST_DUMMY_PASSWORD,
                "Hello", "hello");
        Espresso.pressBack();
    }

    @Test
    public void testSignIn_ForgotPasswordNavigation() {
        testSignUp_SignInNavigation();

        SignInScreen signInScreen = new SignInScreen();
        signInScreen.enterEmailAndPassword(LipConfig.TEST_DUMMY_EMAIL, LipConfig.TEST_DUMMY_PASSWORD);
        signInScreen.clickOnForgotPassword();

        ForgotPasswordScreen forgotPasswordScreen = new ForgotPasswordScreen();
        Espresso.pressBack();
    }
}

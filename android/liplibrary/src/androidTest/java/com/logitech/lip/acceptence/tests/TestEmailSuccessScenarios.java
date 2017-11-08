package com.logitech.lip.acceptence.tests;

import static junit.framework.TestCase.fail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfig;
import com.logitech.lip.LipConfiguration;
import com.logitech.lip.LoginController;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.acceptence.screens.LoginSelectorScreen;
import com.logitech.lip.acceptence.screens.SignInScreen;
import com.logitech.lip.acceptence.screens.SignUpScreen;
import com.logitech.lip.acceptence.screens.TouOptScreen;
import com.logitech.lip.acceptence.tests.utils.CheckActivityFinishedInstruction;
import com.logitech.lip.acceptence.tests.utils.ConditionWatcher;
import com.logitech.lip.acceptence.tests.utils.TestUtils;
import com.logitech.lip.account.AccountManager;
import com.logitech.lip.account.model.AccountToken;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.network.ResponseListener;
import com.logitech.lip.rules.Retry;
import com.logitech.lip.rules.RetryRule;
import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TestEmailSuccessScenarios {
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

    @Rule
    public RetryRule rule = new RetryRule();

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        LipConfiguration configuration = new LipConfiguration.Builder()
                .setServerUrl(LipConfig.SERVER_URL)
                .setPrivacyPolicyUrl(LipConfig.PRIVACY_POLICY_URL)
                .setTermsUseUrl(LipConfig.TOU_OPT_URL)
                .setAnalyticCallback(null)
                .setIsVerifyEmail(false).build();
        if(LIPSdk.getConfiguration() != null) {
            LIPSdk.updateConfiguration(configuration);
        }else {
            LIPSdk.initialize(mActivityTestRule.getActivity().getApplication(),configuration);
        }
        activity = mActivityTestRule.getActivity();

        TestUtils.changeIdlePolicy();
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.unregisterAllIdlingResources();
        activity.finish();
    }

    @Test
    public void testEmailLogin_SuccessScenario() throws Exception {
        LoginSelectorScreen selectorScreen = new LoginSelectorScreen();
        selectorScreen.clickOnEmail();

        SignUpScreen signUpScreen = new SignUpScreen();
        signUpScreen.clickOnAlreadyHaveAccount();

        SignInScreen signInScreen = new SignInScreen();
        signInScreen.enterEmailAndPassword("hello@hello.com", "12345678");
        signInScreen.clickOnLogin();

        /**
         * Idling resource approach will work only when we use check or perform call only
         * At Here there will not be any activity so espresso with throw exception
         * so better approach is check activity closed or not with wait loop
         */
        ConditionWatcher.setTimeoutLimit(ConditionWatcher.DEFAULT_MIN_TIMEOUT_LIMIT);
        ConditionWatcher.waitForCondition(new CheckActivityFinishedInstruction(mActivityTestRule));
    }

    @Test
    @Retry(count = 1)
    public void testEmailCreateAccount_SuccessScenario() throws Exception {
        LoginSelectorScreen selectorScreen = new LoginSelectorScreen();
        selectorScreen.clickOnEmail();

        SignUpScreen signUpScreen = new SignUpScreen();
        String dummyEmail = "abcx"+ new Random().nextInt() + "@gmail.com";
        //String dummyEmail = "hello@hello.com";
        signUpScreen.enterSignUpDetails(dummyEmail, LipConfig.TEST_DUMMY_PASSWORD, "Test", "Test");
        signUpScreen.clickOnCreateAccount();

        TouOptScreen touOptScreen = new TouOptScreen();
        touOptScreen.clickOnTouAccept();
        touOptScreen.clickOnContinue();

        /**
         * Idling resource approach will work only when we use check or perform call only
         * At Here there will not be any activity so espresso with throw exception
         * so better approach is check activity closed or not with wait loop
         */
        ConditionWatcher.setTimeoutLimit(ConditionWatcher.DEFAULT_MIN_TIMEOUT_LIMIT);
        ConditionWatcher.waitForCondition(new CheckActivityFinishedInstruction(mActivityTestRule));
    }


    @Test
    public void testRefreshAccountToken_SuccessScenario() throws Exception {
        // Login first to refresh token
        testEmailLogin_SuccessScenario();

        final CountDownLatch latch = new CountDownLatch(1);
        AccountManager.getCurrentAccountToken(false, new ResponseListener<AccountToken>() {
            @Override
            public void onSuccess(AccountToken result) {
                latch.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode, String errorMessage) {
                latch.countDown();
                fail("Refresh token failed with errorMsg =" + errorMessage);
            }
        });

        latch.await();
    }

    @Test
    public void testForceRefreshAccountToken_SuccessScenario() throws Exception {
        // Login first to refresh token
        testEmailLogin_SuccessScenario();

        final CountDownLatch latch = new CountDownLatch(1);
        AccountManager.getCurrentAccountToken(false, new ResponseListener<AccountToken>() {
            @Override
            public void onSuccess(AccountToken result) {
                latch.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode, String errorMessage) {
                latch.countDown();
                fail("Refresh token failed with errorMsg =" + errorMessage);
            }
        });
        latch.await();
    }

    @Test
    public void testUserInfo_SuccessScenario() throws Exception {
        // Login first to refresh token
        testEmailLogin_SuccessScenario();

        final CountDownLatch latch = new CountDownLatch(1);
        AccountManager.getCurrentAccountToken(false, new ResponseListener<AccountToken>() {
            @Override
            public void onSuccess(AccountToken result) {
                AccountManager.getUserInfo(result.getAccessToken(),
                        new ResponseListener<UserInfo>() {
                            @Override
                            public void onSuccess(UserInfo result) {
                                latch.countDown();
                            }

                            @Override
                            public void onError(ErrorCode errorCode, String errorMessage) {
                                latch.countDown();
                                fail("User Info failed with errorMsg =" + errorMessage);
                            }
                        });
            }

            @Override
            public void onError(ErrorCode errorCode, String errorMessage) {
                latch.countDown();
                fail("Refresh token failed with errorMsg =" + errorMessage);
            }
        });
        latch.await();
    }

    @Test
    public void testSignOut_SuccessScenario() throws Exception {
        // Login first to signout
        testEmailLogin_SuccessScenario();
        final CountDownLatch latch = new CountDownLatch(1);
        LoginController.getInstance().requestLogout(true);

        AccountManager.getCurrentAccountToken(false, new ResponseListener<AccountToken>() {
            @Override
            public void onSuccess(AccountToken result) {
                fail("Not yet signout");
                latch.countDown();
            }

            @Override
            public void onError(ErrorCode errorCode, String errorMessage) {
                latch.countDown();
            }
        });
        latch.await();
    }
}

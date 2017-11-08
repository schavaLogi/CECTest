package com.logitech.lip.suite;

import com.logitech.lip.AccountTokenTrackerTest;
import com.logitech.lip.KeyUtilityTest;
import com.logitech.lip.LIPRequestTest;
import com.logitech.lip.LipSdkTest;
import com.logitech.lip.LoginControllerTest;
import com.logitech.lip.SecPrefTest;
import com.logitech.lip.account.AccountManagerTest;
import com.logitech.lip.ui.EmailVerificationFragTest;
import com.logitech.lip.ui.ForgotPasswordFragTest;
import com.logitech.lip.ui.LoginSelectorFragCreateAccTest;
import com.logitech.lip.ui.LoginSelectorFragmentLoginTest;
import com.logitech.lip.ui.SignInFragTest;
import com.logitech.lip.ui.SignInWithoutValidationTest;
import com.logitech.lip.ui.SignUpFragmentTest;
import com.logitech.lip.ui.TOUFragmentTest;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AccountManagerTest.class,
        AccountTokenTrackerTest.class,
        KeyUtilityTest.class,
        LIPRequestTest.class,
        LipSdkTest.class,
        LoginControllerTest.class,
        SecPrefTest.class,
        EmailVerificationFragTest.class,
        ForgotPasswordFragTest.class,
        LoginSelectorFragCreateAccTest.class,
        LoginSelectorFragmentLoginTest.class,
        SignInFragTest.class,
        SignInWithoutValidationTest.class,
        SignUpFragmentTest.class,
        TOUFragmentTest.class

})
public class LIPTestSuite {
}

package com.logitech.lip.suite;

import com.logitech.lip.AccountTokenTrackerTest;
import com.logitech.lip.KeyUtilityTest;
import com.logitech.lip.LIPRequestTest;
import com.logitech.lip.LipSdkTest;
import com.logitech.lip.LoginControllerTest;
import com.logitech.lip.SecPrefTest;
import com.logitech.lip.account.AccountManagerTest;

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
        SecPrefTest.class
})
public class LipFunctionalTestSuite {

}

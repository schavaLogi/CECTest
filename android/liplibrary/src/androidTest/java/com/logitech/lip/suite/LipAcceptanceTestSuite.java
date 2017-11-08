package com.logitech.lip.suite;

import com.logitech.lip.acceptence.tests.TestEmailFailScenarios;
import com.logitech.lip.acceptence.tests.TestEmailSuccessScenarios;
import com.logitech.lip.acceptence.tests.TestForgotPasswordFailScenario;
import com.logitech.lip.acceptence.tests.TestForgotPasswordSuccessScenario;
import com.logitech.lip.acceptence.tests.TestScreenNavigation;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestEmailFailScenarios.class,
        TestEmailSuccessScenarios.class,
        TestForgotPasswordFailScenario.class,
        TestForgotPasswordSuccessScenario.class,
        TestScreenNavigation.class
})

public class LipAcceptanceTestSuite {
}

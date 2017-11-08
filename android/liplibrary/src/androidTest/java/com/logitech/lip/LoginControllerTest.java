package com.logitech.lip;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.ui.login.LoginSelectorFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginControllerTest {

    private Activity activity;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> activityTestRule =
            new ActivityTestRule<>(LoginSelectorActivity.class);

    @Before
    public void setUp() throws Exception {
        activity = activityTestRule.getActivity();
        LIPSdk.initialize(activity.getApplication(),LipSdkTest.getConfiguration());
    }

    @After
    public void tearDown() throws Exception {
        activity.finish();
    }

    @Test
    public void testAllPublicMethods() throws Exception {
        assertNotNull(LoginController.class.getMethod("getInstance"));
        assertNotNull(LoginController.class.getMethod("getListener"));
        assertNotNull(LoginController.class.getMethod("registerLoginListener", LoginListener.class));
        assertNotNull(LoginController.class.getMethod("requestLogin", Activity.class, LoginOptions.class));

        assertNotNull(LoginController.class.getMethod("requestLogout", boolean.class));
    }

    public void testLoginScreenLaunch() {

        Instrumentation.ActivityMonitor receiverActivityMonitor =
                getInstrumentation().addMonitor(LoginSelectorActivity.class.getName(),
                        null, false);

        final LoginOptions loginOptions = new LoginOptions.Builder().setIsCreate(false).build();
        LoginController.getInstance().requestLogin(activity, loginOptions);

        LoginSelectorActivity receiverActivity = (LoginSelectorActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(10000);

        assertNotNull("ReceiverActivity is null", receiverActivity);

        assertNotNull(activity.findViewById(R.id.btn_facebook));
        assertNotNull(activity.findViewById(R.id.btn_google));
        assertNotNull(activity.findViewById(R.id.btn_email));

        Fragment fragment = activityTestRule.getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.loginContainerHolder);

        assertTrue(fragment instanceof LoginSelectorFragment);

        receiverActivity.finish();

        getInstrumentation().removeMonitor(receiverActivityMonitor);

    }
}

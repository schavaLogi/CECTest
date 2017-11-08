package com.logitech.lip.ui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfig;
import com.logitech.lip.LipConfiguration;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.ui.login.email.SignInFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignInWithoutValidationTest {

    private SignInFragment signInFragment;
    private ViewInteraction emailText, passwordText, submitButton;
    private ViewInteraction emailButton;

    @Rule
    public ActivityTestRule<LoginSelectorActivity> mActivityTestRule =
            new ActivityTestRule<LoginSelectorActivity>(LoginSelectorActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    /** Specify the intent extras that has to be passed to the activity while it
                     * starts */
                    Context targetContext =
                            InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent intent = new Intent(targetContext, LoginSelectorActivity.class);
                    LoginOptions loginOptions = LipConfig.buildLoginOptions(false, false);
                    intent.putExtra(LoginSelectorActivity.LOGIN_OPTIONS, loginOptions);
                    return intent;
                }
            };


    @Before
    public void init() {
        initLIPSDK();
        initUIViews();

        /** Launches the SignIn screen */
        emailButton.perform(click());
        signInFragment = (SignInFragment) getCurrentFragment();
        Espresso.closeSoftKeyboard();
    }

    private void initLIPSDK() {
        LipConfiguration lipConfiguration = LipConfig.getConfiguration();
        LIPSdk.initialize(mActivityTestRule.getActivity().getApplication(), lipConfiguration);
    }

    private void initUIViews() {
        emailButton = onView(
                allOf(withId(R.id.btn_email),
                        isDisplayed()));
        emailText = onView(
                allOf(withId(R.id.sign_in_email),
                        isDisplayed()));

        passwordText = onView(
                allOf(withId(R.id.sign_in_password)
                        , isDisplayed()));

        submitButton = onView(
                allOf(withId(R.id.sign_in_submit)
                        , isDisplayed()));
    }

    private Fragment getCurrentFragment() {
        return mActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag(
                LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Test
    public void signInEmptyPasswordTest() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(clearText());
        submitButton.perform(closeSoftKeyboard(), click());
        assertEquals(signInFragment.getErrResId(), null);
    }
}
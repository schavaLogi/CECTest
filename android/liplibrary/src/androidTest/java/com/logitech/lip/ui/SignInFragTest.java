package com.logitech.lip.ui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.logitech.lip.ui.CustomViewMatchers.withBackground;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.logitech.lip.ui.login.LoginSelectorFragment;
import com.logitech.lip.ui.login.email.ForgotPasswordFragment;
import com.logitech.lip.ui.login.email.SignInFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignInFragTest {

    private SignInFragment signInFragment;

    private ViewInteraction titleBarBack, titleBarText;
    private ViewInteraction emailButton;

    private ViewInteraction emailText, passwordText, forgotPasswordText, submitButton;

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
                    LoginOptions loginOptions = LipConfig.buildLoginOptions(false);
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
        titleBarText = onView(
                allOf(withId(R.id.header_text),
                        isDisplayed()));

        titleBarBack = onView(
                allOf(withId(R.id.left_command),
                        isDisplayed()));

        emailButton = onView(
                allOf(withId(R.id.btn_email),
                        isDisplayed()));

        emailText = onView(
                allOf(withId(R.id.sign_in_email),
                        isDisplayed()));

        passwordText = onView(
                allOf(withId(R.id.sign_in_password)
                        , isDisplayed()));

        forgotPasswordText = onView(
                allOf(withId(R.id.sign_in_forgot)
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
    public void signInIsTitleVisible() {
        titleBarText.check(matches(isDisplayed()));
    }

    @Test
    public void signInIsTitleCorrect() {
        titleBarText.check(matches(withText(R.string.lip_sign_up_login)));
    }

    @Test
    public void signInIsTitleBackDisplayed() {
        titleBarBack.check(matches(isDisplayed()));
    }

    @Test
    public void signInIsTitleBackIconCorrect() {
        Drawable drawable = mActivityTestRule.getActivity().getResources().getDrawable(
                R.drawable.lip_arrow_back_white);
        titleBarBack.check(matches(withBackground(drawable)));
    }

    @Test
    public void signInEmptyCredTest() {
        emailText.perform(clearText());
        passwordText.perform(clearText());
        submitButton.perform(closeSoftKeyboard(), click());
        assertEquals(signInFragment.getErrResId(), mActivityTestRule.getActivity().getResources()
                .getString(R.string.lip_sign_up_error_toast_invalid_email));

    }

    @Test
    public void signInNonFormattedEmailTest() {
        emailText.perform(replaceText("abc"));
        passwordText.perform(clearText());
        submitButton.perform(closeSoftKeyboard(), click());
        assertEquals(signInFragment.getErrResId(), mActivityTestRule.getActivity().getResources()
                .getString(R.string.lip_sign_up_error_toast_invalid_email));
    }

    @Test
    public void signInEmptyPasswordTest() {

        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(clearText());
        submitButton.perform(closeSoftKeyboard(), click());
        assertEquals(signInFragment.getErrResId(), mActivityTestRule.getActivity().getResources()
                .getString(R.string.lip_sign_up_error_toast_enter_pass));
    }

    @Test
    public void signInForgotPasswordLinkTest() {
        forgotPasswordText.perform(click());
        assertThat(getCurrentFragment(), instanceOf(ForgotPasswordFragment.class));
        Espresso.closeSoftKeyboard();
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(SignInFragment.class));
        Espresso.closeSoftKeyboard();
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(LoginSelectorFragment.class));
    }


    @Test
    public void signInForgotPasswordNavigation() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        forgotPasswordText.perform(click());
        assertThat(getCurrentFragment(), instanceOf(ForgotPasswordFragment.class));
        Espresso.closeSoftKeyboard();
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(SignInFragment.class));

        // validate Previous Entered email value exist
        initUIViews();
        emailText.check(matches(withText(LipConfig.TEST_DUMMY_EMAIL)));
        Espresso.closeSoftKeyboard();

        // Go back main screen
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(LoginSelectorFragment.class));
    }


    /**
     * Navigating to login selector screen using hardware back key
     */
    @Test
    public void signInNavigateToLoginSelector() {
        assertThat(getCurrentFragment(), instanceOf(SignInFragment.class));
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(LoginSelectorFragment.class));
    }

    /**
     * Navigating to login selector screen using title back key
     */
    @Test
    public void signInNavigateUsingTitleBar() {
        titleBarBack.check(matches(isDisplayed()));
        titleBarBack.perform(click());
        assertThat(getCurrentFragment(), instanceOf(LoginSelectorFragment.class));
    }

    @Test
    public void signInEmailPrefilledTest() {
        emailText.check(matches(not(isClickable())));
        emailText.check(matches(withText(LipConfig.TEST_DUMMY_EMAIL)));
    }
}

package com.logitech.lip.ui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.logitech.lip.ui.CustomViewMatchers.withBackground;

import static junit.framework.Assert.assertEquals;

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
import com.logitech.lip.ui.login.email.SignInFragment;
import com.logitech.lip.ui.login.email.SignUpFragment;
import com.logitech.lip.ui.login.email.TermsAndConditionFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Note : SW keyboard need to be closed explicitly every time before button click,
 * otherwise the keyboard might still be visible and
 * an event cannot be injected into a window that the process/user does not own
 **/

@RunWith(AndroidJUnit4.class)
public class SignUpFragmentTest {

    private SignUpFragment signUpFragment;

    private ViewInteraction titleBarBack, titleBarText;
    private ViewInteraction emailButton;

    private ViewInteraction emailText, passwordText, firstNameText, lastNameText;
    private ViewInteraction alreadyHaveAnAccText, createAccountButton;

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
                    LoginOptions loginOptions = LipConfig.buildLoginOptions(true);
                    intent.putExtra(LoginSelectorActivity.LOGIN_OPTIONS, loginOptions);
                    return intent;
                }
            };

    @Before
    public void init() {
        initLIPSDK();
        initUIViews();

        /** Launches the SignUp screen */
        emailButton.perform(click());
        signUpFragment = (SignUpFragment) getCurrentFragment();
        Espresso.closeSoftKeyboard();
    }

    private void initLIPSDK() {
        LipConfiguration lipConfiguration = LipConfig.getConfiguration();
        LIPSdk.initialize(mActivityTestRule.getActivity().getApplication(), lipConfiguration);
    }

    private void initUIViews() {

        emailText = onView(
                allOf(withId(R.id.sign_up_email),
                        isDisplayed()));

        passwordText = onView(
                allOf(withId(R.id.sign_up_password)
                        , isDisplayed()));

        firstNameText = onView(
                allOf(withId(R.id.sign_up_firstName),
                        isDisplayed()));

        lastNameText = onView(
                allOf(withId(R.id.sign_up_lastName),
                        isDisplayed()));

        alreadyHaveAnAccText = onView(
                allOf(withId(R.id.sign_up_already_account),
                        isDisplayed()));

        createAccountButton = onView(
                allOf(withId(R.id.sign_up_createButton),
                        isDisplayed()));

        titleBarText = onView(
                allOf(withId(R.id.header_text),
                        isDisplayed()));

        titleBarBack = onView(
                allOf(withId(R.id.left_command),
                        isDisplayed()));

        emailButton = onView(
                allOf(withId(R.id.btn_email),
                        isDisplayed()));
    }

    private Fragment getCurrentFragment() {
        return mActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag(
                LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Test
    public void signUpIsTitleVisible() {
        titleBarText.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsTitleCorrect() {
        titleBarText.check(matches(withText(R.string.lip_sign_up_create_account)));
    }

    @Test
    public void signUpIsTitleBackDisplayed() {
        titleBarBack.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsTitleBackIconCorrect() {
        Drawable drawable = mActivityTestRule.getActivity().getResources().getDrawable(
                R.drawable.lip_arrow_back_white);
        titleBarBack.check(matches(withBackground(drawable)));
    }

    @Test
    public void signUpIsEmailTextVisible() {
        emailText.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsPasswordTextVisible() {
        passwordText.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsFirstNameVisible() {
        firstNameText.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsLastNameVisible() {
        lastNameText.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsAlreadyAccTextVisible() {
        alreadyHaveAnAccText.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsCreateAccButtonVisible() {
        createAccountButton.check(matches(isDisplayed()));
    }

    /**
     * Invalid email test
     */
    @Test
    public void signUpInvalidEmail() {
        emailText.perform(replaceText("abc"));
        createAccountButton.perform(closeSoftKeyboard(), click());
        assertEquals(signUpFragment.getErrResId(), R.string.lip_sign_up_error_toast_invalid_email);
    }

    /**
     * Empty password test
     */
    @Test
    public void signUpEmptyPassword() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(clearText());
        createAccountButton.perform(closeSoftKeyboard(), click());
        assertEquals(signUpFragment.getErrResId(), R.string.lip_sign_up_error_toast_min_count_pwd);
    }

    /**
     * Min password test
     */
    @Test
    public void signUpMinPassword() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(replaceText("abc"));
        createAccountButton.perform(closeSoftKeyboard(), click());
        assertEquals(signUpFragment.getErrResId(), R.string.lip_sign_up_error_toast_min_count_pwd);
    }

    /**
     * Max password test
     */
    @Test
    public void signUpMaxPassword() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(replaceText(
                "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"));
        createAccountButton.perform(closeSoftKeyboard(), click());
        assertEquals(signUpFragment.getErrResId(), R.string.lip_sign_up_error_toast_max_count_pwd);
    }

    /**
     * First name empty test
     */
    @Test
    public void signUpEmptyFirstNameTest() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(replaceText(LipConfig.TEST_DUMMY_PASSWORD));
        firstNameText.perform(clearText());
        createAccountButton.perform(closeSoftKeyboard(), click());
        assertEquals(signUpFragment.getErrResId(),
                R.string.lip_sign_up_error_toast_first_name_missing);
    }

    /**
     * Last name empty test
     */
    @Test
    public void signUpEmptyLastNameTest() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(replaceText(LipConfig.TEST_DUMMY_PASSWORD));
        firstNameText.perform(replaceText("abc"));
        lastNameText.perform(clearText());
        createAccountButton.perform(closeSoftKeyboard(), click());
        assertEquals(signUpFragment.getErrResId(),
                R.string.lip_sign_up_error_toast_last_name_missing);
    }

    /**
     * Navigating to TOU & PP screen
     */
    @Test
    public void signUpNavigateToTOU() {
        emailText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        passwordText.perform(replaceText(LipConfig.TEST_DUMMY_PASSWORD));
        firstNameText.perform(replaceText("abc"));
        lastNameText.perform(replaceText("sdf"));
        createAccountButton.perform(closeSoftKeyboard(), click());
        assertThat(getCurrentFragment(), instanceOf(TermsAndConditionFragment.class));
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(SignUpFragment.class));

        // validate Previous values exist
        initUIViews();
        emailText.check(matches(withText(LipConfig.TEST_DUMMY_EMAIL)));
        passwordText.check(matches(withText(LipConfig.TEST_DUMMY_PASSWORD)));
    }

    /**
     * Navigating to login screen using hardware back key
     */
    @Test
    public void signUpNavigateToLoginScreen() {
        alreadyHaveAnAccText.check(matches(isDisplayed()));
        alreadyHaveAnAccText.perform(click());
        assertThat(getCurrentFragment(), instanceOf(SignInFragment.class));
        Espresso.closeSoftKeyboard();
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(LoginSelectorFragment.class));
    }

    /**
     * Navigating to login selector screen using hardware back key
     */
    @Test
    public void signUpNavigateToLoginSelector() {
        assertThat(getCurrentFragment(), instanceOf(SignUpFragment.class));
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(LoginSelectorFragment.class));
    }

    /**
     * Navigating to login screen using title back key
     */
    @Test
    public void signUpNavigateUsingTitleBar() {
        titleBarBack.check(matches(isDisplayed()));
        titleBarBack.perform(click());
        assertThat(getCurrentFragment(), instanceOf(LoginSelectorFragment.class));
    }
}

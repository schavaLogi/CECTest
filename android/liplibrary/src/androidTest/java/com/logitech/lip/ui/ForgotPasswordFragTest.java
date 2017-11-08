package com.logitech.lip.ui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.logitech.lip.ui.CustomViewMatchers.withBackground;

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
import com.logitech.lip.ui.login.email.ForgotPasswordFragment;
import com.logitech.lip.ui.login.email.SignInFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ForgotPasswordFragTest {

    private ViewInteraction titleBarBack, titleBarText;

    private ViewInteraction emailButton;
    private ViewInteraction forgotPasswordText;

    private ViewInteraction emailEditText, forgotPasswordDesc, sendEmail;

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

        /** Launches the forgot password screen */
        emailButton.perform(click());
        Espresso.closeSoftKeyboard();

        assertThat(getCurrentFragment(), instanceOf(SignInFragment.class));
        forgotPasswordText.perform(click());
        assertThat(getCurrentFragment(), instanceOf(ForgotPasswordFragment.class));
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

        forgotPasswordText = onView(
                allOf(withId(R.id.sign_in_forgot)
                        , isDisplayed()));

        forgotPasswordDesc = onView(
                allOf(withId(R.id.sign_up_email_forgot_instruction),
                        isDisplayed()));

        emailEditText = onView(
                allOf(withId(R.id.sign_up_email_forgot),
                        isDisplayed()));

        sendEmail = onView(
                allOf(withId(R.id.sign_up_send_email)/*,
                        withParent(allOf(withId(R.id.lip_frag_forgot_password_layout)))*/,
                        isDisplayed()));
    }

    private Fragment getCurrentFragment() {
        return mActivityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag(
                LoginSelectorActivity.FRAGMENT_TAG);
    }

    @Test
    public void forgotPwdIsTitleVisible() {
        titleBarText.check(matches(isDisplayed()));
    }

    @Test
    public void forgotPwdIsTitleCorrect() {
        titleBarText.check(matches(withText(R.string.lip_sign_up_forgot_password_title)));
    }

    @Test
    public void forgotPwdIsTitleBackDisplayed() {
        titleBarBack.check(matches(isDisplayed()));
    }

    @Test
    public void forgotPwdIsTitleBackIconCorrect() {
        Drawable drawable = mActivityTestRule.getActivity().getResources().getDrawable(
                R.drawable.lip_arrow_back_white);
        titleBarBack.check(matches(withBackground(drawable)));
    }

    @Test
    public void forgotPwdSendEmail() throws Exception {
        emailEditText.perform(replaceText(LipConfig.TEST_DUMMY_EMAIL));
        sendEmail.perform(closeSoftKeyboard(), click());
    }

    @Test
    public void forgotPwdIsDescVisible() throws Exception {
        forgotPasswordDesc.check(matches(isDisplayed()));
    }

    @Test
    public void forgotPwdIsDescCorrect() throws Exception {
        forgotPasswordDesc.check(
                matches(withText(R.string.lip_sign_up_forgot_password_instruction)));
    }

    /**
     * Navigating to login screen using title back key
     */
    @Test
    public void forgotPwdNavigateUsingTitleBarBack() {
        assertThat(getCurrentFragment(), instanceOf(ForgotPasswordFragment.class));
        titleBarBack.check(matches(isDisplayed()));
        titleBarBack.perform(click());
        assertThat(getCurrentFragment(), instanceOf(SignInFragment.class));
    }

    /**
     * Navigating to login screen using hardware back key
     */
    @Test
    public void forgotPwdNavigateUsingHwBack() {
        assertThat(getCurrentFragment(), instanceOf(ForgotPasswordFragment.class));
        pressBack();
        assertThat(getCurrentFragment(), instanceOf(SignInFragment.class));
    }
}

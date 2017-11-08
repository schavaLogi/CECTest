package com.logitech.lip.ui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.logitech.lip.ui.CustomViewMatchers.withBackground;
import static com.logitech.lip.ui.login.LoginSelectorActivity.FRAGMENT_TAG;

import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfig;
import com.logitech.lip.LipConfiguration;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.ui.login.email.EmailVerifyFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EmailVerificationFragTest {

    private ViewInteraction titleBarBack, titleBarText;
    private ViewInteraction verifyEmailText, emailDesc, verifyEmailDesc, resendEmail;

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
        EmailVerifyFragment fragment = new EmailVerifyFragment();
        mActivityTestRule.getActivity().replaceFragment(fragment, false, FRAGMENT_TAG);

        initLIPSDK();
        initUIViews();
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

        verifyEmailText = onView(
                allOf(withId(R.id.sign_up_verify_email),
                        isDisplayed()));

        emailDesc = onView(
                allOf(withId(R.id.sign_up_verify_desc),
                        isDisplayed()));

        verifyEmailDesc = onView(
                allOf(withId(R.id.sign_up_verify_desc_detailed),
                        isDisplayed()));

        resendEmail = onView(
                allOf(withId(R.id.sign_up_resend_email),
                        isDisplayed()));
    }

    @Test
    public void emailVerifyIsTitleVisible() {
        titleBarText.check(matches(isDisplayed()));
    }

    @Test
    public void emailVerifyIsTitleCorrect() {
        titleBarText.check(matches(withText(R.string.lip_sign_up_confirm_email)));
    }

    @Test
    public void emailVerifyIsTitleBackDisplayed() {
        titleBarBack.check(matches(isDisplayed()));
    }

    @Test
    public void emailVerifyIsTitleBackIconCorrect() {
        Drawable drawable = mActivityTestRule.getActivity().getResources().getDrawable(
                R.drawable.lip_arrow_back_white);
        titleBarBack.check(matches(withBackground(drawable)));
    }

    @Test
    public void VerifyIsEmailVisible() {
        verifyEmailText.perform(CustomViewAction.setTextInTextView(LipConfig.TEST_DUMMY_EMAIL));
        verifyEmailText.check(matches(isDisplayed()));
        verifyEmailText.check(matches(withText(LipConfig.TEST_DUMMY_EMAIL)));
    }

    @Test
    public void emailDescIsDisplayed() {
        emailDesc.check(matches(isDisplayed()));
    }

    @Test
    public void emailDescIsDisplayedCorrectly() {
        emailDesc.check(matches(withText(R.string.lip_sign_up_confirm_email_instruction)));
    }

    @Test
    public void emailVerifyDescIsDisplayed() {
        verifyEmailDesc.check(matches(isDisplayed()));
    }

    @Test
    public void emailVerifyDescIsDisplayedCorrectly() {
        verifyEmailDesc.check(matches(withText(R.string.lip_sign_up_confirm_email_not_received)));
    }

    @Test
    public void emailVerifyResendTest() {
        verifyEmailText.perform(CustomViewAction.setTextInTextView(LipConfig.TEST_DUMMY_EMAIL));
        resendEmail.perform(closeSoftKeyboard(), click());
    }
}

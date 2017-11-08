package com.logitech.lip.ui;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.logitech.lip.ui.CustomViewMatchers.withBackground;
import static com.logitech.lip.ui.CustomViewMatchers.withDrawable;

import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfig;
import com.logitech.lip.LipConfiguration;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.ui.login.LoginSelectorActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginSelectorFragmentLoginTest {

    private ViewInteraction titleBarBack, titleBarText;
    private ViewInteraction facebookButton, googleButton, emailButton;
    private ViewInteraction logo, desc;

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

        logo = onView(
                allOf(withId(R.id.lip_login_logo),
                        withParent(withId(R.id.signInDescription)),
                        isDisplayed()));

        desc = onView(
                allOf(withId(R.id.loginDesc),
                        isDisplayed()));

        facebookButton = onView(
                allOf(withId(R.id.btn_facebook),/*withText(R.string.lip_sign_up_facebook),*/
                        withParent(withId(R.id.signinLayout)),
                        isDisplayed()));

        googleButton = onView(
                allOf(withId(R.id.btn_google),/*withText(R.string.lip_sign_up_google),*/
                        withParent(withId(R.id.signinLayout)),
                        isDisplayed()));

        emailButton = onView(
                allOf(withId(R.id.btn_email), withText("Email"),
                        withParent(withId(R.id.signinLayout)),
                        isDisplayed()));

    }

    @Test
    public void isTitleVisible() {
        titleBarText.check(matches(isDisplayed()));
    }

    @Test
    public void isTitleCorrect() {
        titleBarText.check(matches(withText(R.string.lip_sign_up_login)));
    }

    @Test
    public void isTitleBackDisplayed() {
        titleBarBack.check(matches(isDisplayed()));
    }

    @Test
    public void isTitleBackIconCorrect() {
        Drawable drawable = mActivityTestRule.getActivity().getResources().getDrawable(
                R.drawable.lip_arrow_back_white);
        titleBarBack.check(matches(withBackground(drawable)));
    }

    @Test
    public void facebookButtonIsDisplayed() {
        facebookButton.check(matches(isDisplayed()));
    }

    @Test
    public void facebookButtonClick() {
        facebookButton.perform(click());
    }

    @Test
    public void googleButtonIsDisplayed() {
        googleButton.check(matches(isDisplayed()));
    }

    @Ignore("Espresso will not handle across events")
    //@Test
    public void googleButtonClick() {
        googleButton.perform(click());
        Espresso.pressBack();
    }

    @Test
    public void emailButtonIsDisplayed() {
        emailButton.check(matches(isDisplayed()));
    }

    @Test
    public void emailButtonClick() {
        emailButton.perform(click());
        pressBack();
    }

    @Test
    public void logoIsDisplayed() {
        logo.check(matches(isDisplayed()));
    }

    @Test
    public void logoIsDisplayedCorrectly() {
        Drawable drawable = mActivityTestRule.getActivity().getResources().getDrawable(
                R.drawable.lip_logi_logo);
        logo.check(matches(withDrawable(drawable)));
    }

    @Test
    public void signUpIsDescVisible() {
        desc.check(matches(isDisplayed()));
    }

    @Test
    public void signUpIsDescCorrect() {
        desc.check(matches(withText(R.string.lip_login_with)));
    }

}

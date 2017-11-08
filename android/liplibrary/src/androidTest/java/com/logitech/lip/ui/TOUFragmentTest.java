package com.logitech.lip.ui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static com.logitech.lip.ui.CustomViewMatchers.withBackground;
import static com.logitech.lip.ui.login.LoginSelectorActivity.FRAGMENT_TAG;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.LipConfig;
import com.logitech.lip.LipConfiguration;
import com.logitech.lip.LoginOptions;
import com.logitech.lip.R;
import com.logitech.lip.account.model.UserInfo;
import com.logitech.lip.ui.login.LoginSelectorActivity;
import com.logitech.lip.ui.login.email.TermsAndConditionFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by nkumar3 on 6/27/2016.
 */

public class TOUFragmentTest {

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
    private TermsAndConditionFragment touFragment;
    private ViewInteraction titleBarBack, titleBarText;
    private ViewInteraction touDesc, acceptDesc, acceptCheckBox, receiveDesc, receiveCheckBox,
            continueButton;

    @Before
    public void init() {
        initLIPSDK();
        initUIViews();

        UserInfo userInfo = new UserInfo(LipConfig.TEST_DUMMY_EMAIL, LipConfig.TEST_DUMMY_PASSWORD,
                true);
        userInfo.setFirstName("aaa");
        userInfo.setLastName("bbb");

        /** Launches the TOU & PP screen */
        touFragment = TermsAndConditionFragment.newInstance(null, userInfo);
        mActivityTestRule.getActivity().replaceFragment(touFragment, false, FRAGMENT_TAG);
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

        touDesc = onView(
                allOf(withId(R.id.tou_opt_container),
                        isDisplayed()));

        acceptDesc = onView(
                allOf(withId(R.id.sign_up_accept_checkbox_text),
                        isDisplayed()));

        acceptCheckBox = onView(
                allOf(withId(R.id.sign_up_accept_button),
                        isDisplayed()));

        receiveDesc = onView(
                allOf(withId(R.id.sign_up_keep_button_text),
                        isDisplayed()));

        receiveCheckBox = onView(
                allOf(withId(R.id.sign_up_keep_button),
                        isDisplayed()));

        continueButton = onView(
                allOf(withId(R.id.terms_accept),
                        isDisplayed()));
    }

    @Test
    public void touIsTitleVisible() {
        titleBarText.check(matches(isDisplayed()));
    }

    @Test
    public void touIsTitleCorrect() {
        titleBarText.check(matches(withText(R.string.lip_sign_up_welcome)));
    }

    @Test
    public void touIsTitleBackDisplayed() {
        titleBarBack.check(matches(isDisplayed()));
    }

    @Test
    public void touIsTitleBackIconCorrect() {
        Drawable drawable = mActivityTestRule.getActivity().getResources().getDrawable(
                R.drawable.lip_arrow_back_white);
        titleBarBack.check(matches(withBackground(drawable)));
    }

    @Test
    public void isTOUDescDisplayed() {
        touDesc.check(matches(isDisplayed()));
    }

    @Test
    public void isAccDescDisplayed() {
        acceptDesc.check(matches(isDisplayed()));
    }

    @Test
    public void isAccChkBoxDisplayed() {
        acceptCheckBox.check(matches(isDisplayed()));
    }

    @Test
    public void isReceiveDescDisplayed() {
        receiveDesc.check(matches(isDisplayed()));
    }

    @Test
    public void isReceiveChkBoxDisplayed() {
        receiveCheckBox.check(matches(isDisplayed()));
    }

    @Test
    public void continueButtonClickedWithoutAccepting() {
        VolleyIdlingResource volleyResources = null;
        try {
            volleyResources = new VolleyIdlingResource("VolleyCalls");
            registerIdlingResources(volleyResources);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        continueButton.check(matches(isDisplayed()));
        continueButton.perform(closeSoftKeyboard(), click());
        assertEquals(touFragment.getErrResId(), mActivityTestRule.getActivity().getResources()
                .getString(R.string.lip_sign_up_error_toast_accept_conditions));

        unregisterIdlingResources(volleyResources);
    }

    @Test
    public void continueButtonClicked() {

        VolleyIdlingResource volleyResources = null;
        try {
            volleyResources = new VolleyIdlingResource("VolleyCalls");
            registerIdlingResources(volleyResources);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        acceptCheckBox.perform(click());
        continueButton.perform(click());

        unregisterIdlingResources(volleyResources);
    }
}

package com.logitech.lip.acceptence.screens;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.action.ViewActions;

import com.logitech.lip.R;

public class ForgotPasswordScreen {

    public ForgotPasswordScreen() {
        onView(withId(R.id.sign_up_email_forgot_instruction)).check(matches(isDisplayed()));
    }

    public void enterEmail(String email) {
        onView(withId(R.id.sign_up_email_forgot)).check(matches(isDisplayed()))
                .perform(clearText()).perform(replaceText(email), ViewActions.closeSoftKeyboard());
    }

    public void clickOnSendEmail() {
        onView(withId(R.id.sign_up_send_email)).check(matches(isDisplayed()))
                .perform(click());
    }
}

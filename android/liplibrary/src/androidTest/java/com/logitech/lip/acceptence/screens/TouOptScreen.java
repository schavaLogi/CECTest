package com.logitech.lip.acceptence.screens;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import com.logitech.lip.R;

public class TouOptScreen {
    public TouOptScreen() {
        onView(withId(R.id.tou_opt_container)).check(matches(isDisplayed()));
    }

    public void clickOnTouAccept() {
        onView(withId(R.id.sign_up_accept_button)).check(matches(isDisplayed()))
                .perform(click());
    }

    public void clickOnReceiveUpdates() {
        onView(withId(R.id.sign_up_keep_button)).check(matches(isDisplayed()))
                .perform(click());
    }

    public void clickOnContinue() {
        onView(withId(R.id.terms_accept)).check(matches(isDisplayed()))
                .perform(click());
    }
}

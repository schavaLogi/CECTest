package com.logitech.lip.acceptence.screens;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import com.logitech.lip.R;

public class ErrorScreen {
    public ErrorScreen() {
        onView(withId(R.id.lip_error_desc)).check(matches(isDisplayed()));
    }

    public void clickOnPositiveButton() {
        onView(withId(R.id.lip_error_positive)).check(matches(isDisplayed()))
                .perform(click());
    }

    public void clickOnNegativeButton() {
        onView(withId(R.id.lip_error_negative)).check(matches(isDisplayed()))
                .perform(click());
    }
}

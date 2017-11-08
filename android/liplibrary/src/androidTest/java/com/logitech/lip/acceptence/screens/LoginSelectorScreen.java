package com.logitech.lip.acceptence.screens;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;

import static org.hamcrest.Matchers.allOf;

import com.logitech.lip.R;

public class LoginSelectorScreen {

    public LoginSelectorScreen() {
        onView(allOf(withId(R.id.lip_login_logo), withParent(withId(R.id.signInDescription))))
                .check(matches(isDisplayed()));
    }

    public void clickOnEmail() {
        onView(allOf(withId(R.id.btn_email), withParent(withId(R.id.signinLayout))))
                .check(matches(isDisplayed()))
                .perform(click());

    }
}

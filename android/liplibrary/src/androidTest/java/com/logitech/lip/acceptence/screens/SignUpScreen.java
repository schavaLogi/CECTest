package com.logitech.lip.acceptence.screens;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;

import com.logitech.lip.R;

public class SignUpScreen {

    public SignUpScreen() {
        onView(withId(R.id.sign_up_email)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_firstName)).check(matches(isDisplayed()));
    }

    public void enterSignUpDetails(String email, String password, String firstName, String lastName) {
        onView(withId(R.id.sign_up_email)).check(matches(isDisplayed()))
                .perform(clearText(), replaceText(email));

        onView(withId(R.id.sign_up_password)).check(matches(isDisplayed()))
                .perform(clearText(), replaceText(password));

        onView(withId(R.id.sign_up_firstName)).check(matches(isDisplayed()))
                .perform(clearText(), replaceText(firstName));

        onView(withId(R.id.sign_up_lastName)).check(matches(isDisplayed()))
                .perform(clearText(), replaceText(lastName), ViewActions.closeSoftKeyboard());
    }

    public void clickOnCreateAccount() {
        onView(withId(R.id.sign_up_createButton)).check(matches(isDisplayed()))
                .perform(ViewActions.closeSoftKeyboard(), click());
    }

    public void clickOnAlreadyHaveAccount() {
        // Close keyboard to ensure lookup id is visible in screen
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.sign_up_already_account)).check(matches(isDisplayed()))
                .perform(click());
    }
}

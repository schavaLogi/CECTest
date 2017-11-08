package com.logitech.lip.acceptence.screens;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;

import com.logitech.lip.R;

public class SignInScreen {

    public SignInScreen() {
        onView(withId(R.id.sign_in_email)).check(matches(isDisplayed()));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.sign_in_submit)).check(matches(isDisplayed()));
    }

    public void enterEmailAndPassword(String email, String password) {
        ViewInteraction emailEdit = onView(allOf(withId(R.id.sign_in_email), isDisplayed()));
        emailEdit.perform(clearText());

        emailEdit.perform(replaceText(email));

        ViewInteraction passwordEdit = onView(allOf(withId(R.id.sign_in_password), isDisplayed()));
        passwordEdit.perform(clearText());

        passwordEdit.perform(replaceText(password));
    }

    public void clickOnLogin() {
        onView(withId(R.id.sign_in_submit)).check(matches(isDisplayed()))
                .perform(closeSoftKeyboard(),click());
    }

    public void clickOnForgotPassword() {
        onView(withId(R.id.sign_in_forgot)).check(matches(isDisplayed()))
                .perform(closeSoftKeyboard(),click());
    }

    public void clickOnNewAccount() {
        onView(withId(R.id.sign_in_need_account)).check(matches(isDisplayed()))
                .perform(closeSoftKeyboard(),click());
    }
}

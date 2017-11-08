package com.logitech.lip.acceptence.tests.utils;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class TestUtils {

    public static void changeIdlePolicy() {
        // Change IdlingPolicies to 30 sec. Default is 26 sec
        IdlingPolicies.setMasterPolicyTimeout(
                1000 * 30, TimeUnit.MILLISECONDS);

        IdlingPolicies.setIdlingResourceTimeout(
                1000 * 30, TimeUnit.MILLISECONDS);
    }

    public static void unregisterAllIdlingResources () {
        List<IdlingResource> idlingResourceList = Espresso.getIdlingResources();
        if (idlingResourceList != null) {
            for (int i = 0; i < idlingResourceList.size(); i++) {
                Espresso.unregisterIdlingResources(idlingResourceList.get(i));
            }
        }
    }
}

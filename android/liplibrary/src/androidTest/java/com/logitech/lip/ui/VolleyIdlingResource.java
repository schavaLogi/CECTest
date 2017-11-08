package com.logitech.lip.ui;


import static android.support.test.espresso.web.deps.guava.base.Preconditions.checkNotNull;

import static com.logitech.lip.LIPSdk.getRequestQueue;

import android.os.Handler;
import android.support.test.espresso.IdlingResource;
import android.util.Log;

import com.logitech.lip.volley.Request;
import com.logitech.lip.volley.RequestQueue;

import java.lang.reflect.Field;
import java.util.Set;


/**
 * Created by nkumar3 on 7/18/2016.
 * A custom IdlingResource for monitoring the Volley request queue and synchronize tests execution
 */

public class VolleyIdlingResource implements IdlingResource {

    private static final String TAG = VolleyIdlingResource.class.getSimpleName();

    private static final int IDLE_POLL_DELAY_MILLIS = 200;

    private final String resourceName;
    private ResourceCallback resourceCallback;

    private RequestQueue mVolleyRequestQueue;
    private Field mCurrentRequests;

    public VolleyIdlingResource(String resourceName) throws NoSuchFieldException {
        this.resourceName = checkNotNull(resourceName);

        mVolleyRequestQueue = getRequestQueue();

        mCurrentRequests = RequestQueue.class.getDeclaredField("mCurrentRequests");
        mCurrentRequests.setAccessible(true);
    }

    @Override
    public String getName() {
        return resourceName;
    }

    @Override
    public boolean isIdleNow() {
        try {
            Set<Request> set = (Set<Request>) mCurrentRequests.get(mVolleyRequestQueue);
            int count = set.size();
            if (set != null) {

                if (count == 0) {
                    Log.d(TAG, "Volley is idle now! with: " + count);
                    resourceCallback.onTransitionToIdle();
                } else {
                    Log.d(TAG, "Not idle... " +count);
                    // To run every 200 msec, Espresso calls every 5 sec
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isIdleNow();
                        }
                    }, IDLE_POLL_DELAY_MILLIS);
                }
                return count == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}

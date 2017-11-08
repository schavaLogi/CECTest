package com.logitech.lip.ui.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseActivity extends FragmentActivity {
    private boolean isResumed = false;

    protected List<PendingUIRequest> pendingUIRequests;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isResumed = false;
        clearPendingRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isResumed = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public boolean isActivityResumed(){
        return isResumed;
    }

    protected void clearPendingRequests() {
        if (pendingUIRequests != null) {
            pendingUIRequests.clear();
        }
    }

    /**
     * Add requests to process later (Can be used to fragment transition after saveInstanceState)
     *
     * @param pendingRequest
     */
    protected void addPendingUIRequest(PendingUIRequest pendingRequest) {
        if(pendingUIRequests == null)
            pendingUIRequests = new ArrayList<>(4);

        if(pendingRequest != null)
            pendingUIRequests.add(pendingRequest);
    }

    /**
     * Will execute Requests in sequence after fragment is ready
     */
    protected void executePendingUIRequests(){
        if(pendingUIRequests == null) return;
        Iterator<PendingUIRequest> r = pendingUIRequests.iterator();
        while(r.hasNext()) {
            PendingUIRequest p = r.next();
            r.remove();
            executeInternal(p);
        }
    }

    protected void executeInternal(PendingUIRequest p) {
    }

    public static class PendingUIRequest {
        public Fragment fragment;
        public boolean isReplace;
        public boolean addToBackStack;
        public String tag;
        public boolean isPopBackstack;
    }
}

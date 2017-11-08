package com.logitech.lip.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logitech.lip.ui.common.CustomToast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseFragment extends Fragment {

    public CustomToast customToast;

    protected List<PendingUIRequest> pendingUIRequests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(pendingUIRequests != null) {
            pendingUIRequests.clear();
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Add requests to process later (Can be used to fragment transition after saveInstanceState)
     *
     * @param pendingRequest
     */
    protected void addPendingUIRequest(PendingUIRequest pendingRequest) {
        if(pendingUIRequests == null)
            pendingUIRequests = new ArrayList<>(4);
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

    /**
     * Actual logic to process each request
     * @param p
     */
    protected void executeInternal(PendingUIRequest p) {
    }

    public static class PendingUIRequest {
        public int arg1;
        public int what;
        public Object data;
    }
}

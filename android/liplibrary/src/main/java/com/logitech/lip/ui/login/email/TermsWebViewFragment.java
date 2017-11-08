/*
 * Copyright (c) 2015 Logitech, Inc. All Rights Reserved
 *
 * This program is a trade secret of LOGITECH, and it is not to be reproduced,
 * published, disclosed to others, copied, adapted, distributed or displayed
 * without the prior written authorization of LOGITECH.
 *
 * Licensee agrees to attach or embed this notice on all copies of the program
 * including partial copies or modified versions thereof.
 */
package com.logitech.lip.ui.login.email;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.logitech.lip.R;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.login.BackNavigationListener;
import com.logitech.lip.ui.login.BaseFragment;

import java.net.URI;

public class TermsWebViewFragment extends BaseFragment {

    public static final int URL_TYPE_TOU = 0;
    public static final int URL_TYPE_PRIVACY_POLICY = 1;
    public static final int URL_TYPE_GENERIC = 2;

    private int urlType = URL_TYPE_GENERIC;
    private String loadUrl;
    private ProgressBar progressBar;
    private WebView webView;

    private BackNavigationListener backNavigationListener;

    public static TermsWebViewFragment newInstance(int urlType, String url) {
        TermsWebViewFragment fragment = new TermsWebViewFragment();
        fragment.setUrl(urlType, url);
        return fragment;
    }
    /**
     * set url to show the content in webview
     * @param url url to load content
     */
    private void setUrl(int urlType, String url) {
        this.urlType = urlType;
        loadUrl = url;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            backNavigationListener = (BackNavigationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement TermsConditionUiNavigationListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lip_frag_terms_webview, container, false);

        initTitleBar(rootView);

        initUiControls(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        URI uri = URI.create(loadUrl);
        if (savedInstanceState!=null) {
            webView.restoreState(savedInstanceState);
        } else if (uri !=null) {
            webView.loadUrl(uri.toASCIIString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) {
            webView.saveState(outState);
        }
    }

    private void initTitleBar(View rootView) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        String title;
        switch (urlType) {
            case URL_TYPE_TOU:
                title = getString(R.string.lip_sign_up_terms_use);
                break;
            case URL_TYPE_PRIVACY_POLICY:
                title = getString(R.string.lip_sign_up_privacy_policy);
                break;
            default:
                title = getString(R.string.lip_sign_up_welcome);
                break;
        }
        titleBar.setTitle(title.toUpperCase())
                .setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (webView!= null && webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            backNavigationListener.showPreviousScreen();
                        }
                    }
                });
    }



    private void initUiControls(View rootView) {
        progressBar = (ProgressBar) rootView.findViewById(R.id.sign_up_terms_conditions_progress);
        progressBar.setVisibility(View.VISIBLE);

        webView = (WebView) rootView.findViewById(R.id.sign_up_terms_conditions);

        WebSettings webSettings = webView.getSettings();

        webSettings.setLoadsImagesAutomatically(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TermsConditionView webViewClient = new TermsConditionView();
        webView.setWebViewClient(webViewClient);

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView != null && webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    private class TermsConditionView extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /*@TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }*/

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:var footer = document.getElementById(\"footer\"); footer.parentNode.removeChild(footer); var header = document.getElementById(\"header-full\"); header.parentNode.removeChild(header);");
            progressBar.setVisibility(View.GONE);
        }
    }
}

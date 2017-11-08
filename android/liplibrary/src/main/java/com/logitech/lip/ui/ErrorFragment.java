package com.logitech.lip.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.logitech.lip.R;
import com.logitech.lip.ui.common.CustomTitleBar;
import com.logitech.lip.ui.login.BaseFragment;
import com.logitech.lip.ui.login.LoginSelectorActivity;

/**
 * All the important errors that deserves the user attention
 * are displayed in a separate screen.
 *
 * Created by nkumar3 on 4/7/2016.
 */
public class ErrorFragment extends BaseFragment {

    public static final String HEADER_TITLE = "header_title";
    public static final String ERROR_TITLE = "error_title";
    public static final String ERROR_DESC = "error_desc";
    public static final String POSITIVE_BUTTON = "positiveButton";
    public static final String NEGATIVE_BUTTON = "negativeButton";

    public static final String REQ_ERROR_CODE = "reqCode";

    private String headerTitle;
    private String titleText, descText;
    private String positiveButtonText, negativeButtonText;
    private int reqCode;

    private LoginSelectorActivity parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (LoginSelectorActivity) context;
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lip_frag_error, container, false);

        if (getArguments() != null) {
            headerTitle = getArguments().getString(HEADER_TITLE);
            titleText = getArguments().getString(ERROR_TITLE);
            descText = getArguments().getString(ERROR_DESC);
            positiveButtonText = getArguments().getString(POSITIVE_BUTTON);
            negativeButtonText = getArguments().getString(NEGATIVE_BUTTON);
            reqCode = getArguments().getInt(REQ_ERROR_CODE);
        }

        initTitleBar(rootView, headerTitle);

        if (titleText != null && !titleText.isEmpty()) {
            TextView errorTitle = (TextView) rootView.findViewById(R.id.lip_error_title);
            errorTitle.setVisibility(View.VISIBLE);
            errorTitle.setText(titleText);
        }

        if (descText != null && !descText.isEmpty()) {
            TextView errorDesc = (TextView) rootView.findViewById(R.id.lip_error_desc);
            errorDesc.setVisibility(View.VISIBLE);
            errorDesc.setText(descText);
        }

        if (positiveButtonText != null && !positiveButtonText.isEmpty()) {
            Button positiveButton = (Button) rootView.findViewById(R.id.lip_error_positive);
            positiveButton.setVisibility(View.VISIBLE);
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        if (negativeButtonText != null && !negativeButtonText.isEmpty()) {
            Button negativeButton = (Button) rootView.findViewById(R.id.lip_error_negative);
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerKeyListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initTitleBar(View rootView, String title) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        if (titleBar != null) {
            if (!TextUtils.isEmpty(title)) {
                titleBar.setTitle(title.toUpperCase());
                titleBar.setLeftIcon(R.drawable.lip_arrow_back_white, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleBackKey();
                    }
                });
            } else {
                titleBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void handleBackKey() {
        parentActivity.onBackPressed();
    }

    private void registerKeyListener() {
        if(getView() != null) {
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                        handleBackKey();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}


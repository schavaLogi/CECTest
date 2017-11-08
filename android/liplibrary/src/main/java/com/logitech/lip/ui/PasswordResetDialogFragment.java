package com.logitech.lip.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.logitech.lip.R;
import com.logitech.lip.ui.common.CustomTitleBar;

public class PasswordResetDialogFragment extends DialogFragment {

    public static final int ERROR_PASSWORD_RESET = 6767;

    public static final String HEADER_TITLE = "header_title";
    public static final String ERROR_TITLE = "error_title";
    public static final String ERROR_DESC = "error_desc";
    public static final String POSITIVE_BUTTON = "positiveButton";
    public static final String NEGATIVE_BUTTON = "negativeButton";
    public static final String REQ_ERROR_CODE = "reqCode";

    private String headerTitle;
    private String titleText, descText;
    private String positiveButtonText, negativeButtonText;

    public static PasswordResetDialogFragment newInstance(Bundle bundle) {
        PasswordResetDialogFragment frag = new PasswordResetDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.lip_frag_error, null);

        if(getArguments() != null) {
            headerTitle = getArguments().getString(HEADER_TITLE);
            titleText = getArguments().getString(ERROR_TITLE);
            descText = getArguments().getString(ERROR_DESC);
            positiveButtonText = getArguments().getString(POSITIVE_BUTTON);
            negativeButtonText = getArguments().getString(NEGATIVE_BUTTON);
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
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                Activity.RESULT_OK,
                                getActivity().getIntent());

                        PasswordResetDialogFragment.this.dismiss();
                    }
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
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                Activity.RESULT_CANCELED,
                                getActivity().getIntent());
                        PasswordResetDialogFragment.this.dismiss();
                    }
                }
            });
        }

        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(rootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(rootView);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                Activity.RESULT_CANCELED,
                                getActivity().getIntent());
                        return true;
                    }
                }
                return false;
            }
        });
        return dialog;
    }

    private void initTitleBar(View rootView, String title) {
        CustomTitleBar titleBar = (CustomTitleBar) rootView.findViewById(R.id.header);
        if (titleBar != null) {
            if (!TextUtils.isEmpty(title)) {
                titleBar.setTitle(title.toUpperCase());
                titleBar.setLeftIcon(-1, null);
            } else {
                titleBar.setVisibility(View.INVISIBLE);
            }
        }
    }

}


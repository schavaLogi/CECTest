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
package com.logitech.lip.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.logitech.lip.R;

public class ProgressDialog extends Dialog {
    private final TextView textView;

    public ProgressDialog(Context context) {
        super(context, R.style.lip_progressDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lip_progress_dialog);
        if(getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        textView = (TextView) findViewById(R.id.progressTitle);

        ImageView spinner = (ImageView) findViewById(R.id.spinnerProgress);
        spinner.setBackgroundResource(R.drawable.lip_animate_progress_spinner);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) spinner.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

    /**
     * set the title for dialog
     *
     * @param title title
     */
    public void setProgressTitle(String title) {
        textView.setText(title);
    }

    public void setProgressTitle(int titleId) {
        textView.setText(textView.getResources().getString(titleId));
    }
}

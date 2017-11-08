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

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.logitech.lip.R;


public class CustomToast {
    private final Toast toast;
    private final TextView toastText;

    public CustomToast(Context context){
        toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.lip_layout_toast, null);
        toastText = (TextView) view.findViewById(R.id.toast_Text);
        toast.setView(view);
    }

    /**
     * set the toast message
     * @param message message
     */
    public void showToast(String message){
        toastText.setText(message);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public void showToast(String message, boolean isShort){
        toastText.setText(message);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
        if(isShort) {
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    public void cancel() {
        if(toast != null) {
            toast.cancel();
        }
    }
}

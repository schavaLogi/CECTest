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
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logitech.lip.R;


public class CustomTitleBar extends RelativeLayout {
    private final Context context;
    private TextView titleTxt;
    private ImageView leftIcon;
    private ImageView rightIcon;
    private RelativeLayout leftIconContainer;

    public CustomTitleBar(Context context) {
        super(context);
        this.context = context;
        inflateHeader();
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateHeader();
    }

    public CustomTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateHeader();
    }


    private void inflateHeader() {
        View rootView = View.inflate(getContext(), R.layout.lip_titlebar, this);
        leftIcon = (ImageView)rootView.findViewById(R.id.left_command);
        rightIcon = (ImageView)rootView.findViewById(R.id.right_command);
        titleTxt = (TextView)rootView.findViewById(R.id.header_text);
        leftIconContainer  = (RelativeLayout)rootView.findViewById(R.id.left_command_container);
    }

    public CustomTitleBar setTitle(int title) {
        titleTxt.setText(title);
        return this;
    }

    public CustomTitleBar setTitle(CharSequence title) {
        titleTxt.setText(title);
        return this;
    }

    public TextView getTitleView() {
        return titleTxt;
    }

    public CustomTitleBar setLeftIcon(int resId, OnClickListener onLeftClick) {
        if(-1 == resId)
            leftIcon.setVisibility(INVISIBLE);
        else{
            leftIcon.setVisibility(VISIBLE);
            leftIcon.setBackgroundResource(resId);
            leftIconContainer.setOnClickListener(onLeftClick);
        }
        return this;
    }

    public CustomTitleBar setRightIcon(int resId, OnClickListener onRightClick) {
        if(-1 == resId) {
            rightIcon.setVisibility(INVISIBLE);
        } else{
            rightIcon.setVisibility(VISIBLE);
            rightIcon.setOnClickListener(onRightClick);
            rightIcon.setBackgroundResource(resId);
        }
        return this;
    }
}

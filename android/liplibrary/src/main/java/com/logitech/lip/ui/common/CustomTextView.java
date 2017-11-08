/*
 *
 *  Copyright (c) 2015 Logitech, Inc. All Rights Reserved
 *
 *  This program is a trade secret of LOGITECH, and it is not to be reproduced,
 *  published, disclosed to others, copied, adapted, distributed or displayed
 *  without the prior written authorization of LOGITECH.
 *
 *  Licensee agrees to attach or embed this notice on all copies of the program
 *  including partial copies or modified versions thereof.
 *
 */

package com.logitech.lip.ui.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class CustomTextView extends TextView{
	public CustomTextView(Context context){
		super(context);
		setCustomTypeface(context);
	}
	public CustomTextView(Context context, AttributeSet attrs){
		super(context, attrs);
		setCustomTypeface(context);
	}
	public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		setCustomTypeface(context);
	}

	private void setCustomTypeface(Context context){
		TypefaceManager manager = TypefaceManager.getInstance();
		if(manager != null) {
			Typeface typeface= manager.getCustomTypeface(context, this.getTypeface());
			setTypeface(typeface);
		}
	}
}
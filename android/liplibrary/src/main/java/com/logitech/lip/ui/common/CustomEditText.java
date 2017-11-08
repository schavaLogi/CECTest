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
import android.widget.EditText;


public class CustomEditText extends EditText{

	public CustomEditText(Context context){
		super(context);
		Typeface typeface = TypefaceManager.getInstance().getCustomTypeface(context, this.getTypeface());
		setTypeface(typeface);
	}
	public CustomEditText(Context context, AttributeSet attrs){
		super(context, attrs);
		Typeface typeface = TypefaceManager.getInstance().getCustomTypeface(context, this.getTypeface());
		setTypeface(typeface);
	}
	public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		Typeface typeface = TypefaceManager.getInstance().getCustomTypeface(context, this.getTypeface());
		setTypeface(typeface);
	}

	private void setCustomTypeface(Context context){
		TypefaceManager manager = TypefaceManager.getInstance();
		if(manager != null) {
			Typeface typeface= manager.getCustomTypeface(context, this.getTypeface());
			setTypeface(typeface);
		}
	}
}
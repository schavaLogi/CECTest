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

import com.logitech.lip.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps in caching various types of the app specific font.
 * The code is derived based on the following link
 *
 * http://anton.averin.pro/2012/09/12/how-to-use-android-roboto-font-in-honeycomb-and-earlier-versions/#sthash.GtHheICE.dpuf
 *
 * Note: Modified for LIP to fallback to default font passed by application,
 * 	     App font not available it will fetch default android system font
 * @author Jana
 */
public class TypefaceManager{

	private static final String TAG = TypefaceManager.class.getSimpleName();

	public interface FontTypes {
		String REGULAR = "Regular";
		String BOLD = "Bold";
		String ITALIC = "Italic";
	}

	/* Default font used when application not passed any font */
	public static String[] defaultFontSet = {"fonts/BrownProTT-Regular.ttf",
			"fonts/BrownProTT-Bold.ttf", "fonts/BrownProTT-Light.ttf"};

	private static TypefaceManager INSTANCE;
	/**
	 * map of font types to font paths in assets
	 */
	private final Map<String, String> fontMap = new HashMap<>();

	private final Map<String, String> defaultFontMap = new HashMap<>();

	/* cache for loaded Custom typefaces */
	private static final Map<String, Typeface> typefaceCache = new HashMap<>();

	/* Cache for loaded Default typefaces */
	private static final Map<String, Typeface> defaultTypefaceCache = new HashMap<>();

	private TypefaceManager(Map<String, String> customFont, Map<String, String> defaultFont) {
		fontMap.putAll(customFont);
		defaultFontMap.putAll(defaultFont);
	}

	public static void initialize(Map<String,String> customFont) {
		if(INSTANCE == null) {
			// Initialize Default set
			Map<String, String > defaultSet = new HashMap<>();
			if(defaultFontSet != null && defaultFontSet.length >0 ) {
				defaultSet.put(FontTypes.REGULAR, defaultFontSet[0]);
				defaultSet.put(FontTypes.BOLD, defaultFontSet[1]);
				defaultSet.put(FontTypes.ITALIC, defaultFontSet[2]);
			}
			INSTANCE = new TypefaceManager(customFont, defaultSet);
		}
	}

	public static void initialize(String[] customFontSet) {
		if(INSTANCE == null) {
			Map<String, String > customSet = new HashMap<>();
			if(customFontSet != null && customFontSet.length > 0) {
				//for( String font : customFontSet) {
				customSet.put(FontTypes.REGULAR, customFontSet[0]);
				customSet.put(FontTypes.BOLD, customFontSet[1]);
				customSet.put(FontTypes.ITALIC, customFontSet[2]);
				//}
			} else {
				customSet.put(FontTypes.REGULAR, defaultFontSet[0]);
				customSet.put(FontTypes.BOLD, defaultFontSet[1]);
				customSet.put(FontTypes.ITALIC, defaultFontSet[2]);
			}

			// Initialize Default set
			Map<String, String > defaultSet = new HashMap<>();
			if(defaultFontSet != null && defaultFontSet.length >0 ) {
				defaultSet.put(FontTypes.REGULAR, defaultFontSet[0]);
				defaultSet.put(FontTypes.BOLD, defaultFontSet[1]);
				defaultSet.put(FontTypes.ITALIC, defaultFontSet[2]);
			}

			INSTANCE = new TypefaceManager(customSet, defaultSet);
		}
	}

	public static TypefaceManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Creates custom typeface and puts it into cache
	 *
	 * @param context Application or Activity Context
	 * @param fontType Font type (Regular, Bold, Light)
	 * @return Typeface based on passed parameter
	 */
	private Typeface getCustomTypeface(Context context, String fontType) {
		String fontPath = fontMap.get(fontType);
		try{
			if (!typefaceCache.containsKey(fontType)) {
				typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
			}
		} catch (Exception e) {
			Logger.debug(TAG, "getCustomTypeface", "Couldn't create typeface from the given font fontPath=" + fontPath);
		}

		return typefaceCache.get(fontType);
	}

	private Typeface getDefaultTypeface(Context context, String fontType) {
		String fontPath = defaultFontMap.get(fontType);
		try{
			if (!defaultTypefaceCache.containsKey(fontType)) {
				defaultTypefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
			}
		} catch (Exception e) {
			Logger.debug(TAG, "getDefaultTypeface","Couldn't create typeface from the given font fontPath=" + fontPath);
		}

		return defaultTypefaceCache.get(fontType);
	}

	/**
	 * Gets typeface according to passed typeface style settings. Will get font for Typeface.BOLD etc
	 *
	 * @param context Application or Activity Context
	 * @param originalTypeface Original Typeface as defined for the view
	 * @return Typeface based on passed parameter
	 */
	public Typeface getCustomTypeface(Context context, Typeface originalTypeface) {
		int style = originalTypeface != null ? originalTypeface.getStyle() : Typeface.NORMAL;
		return getCustomTypeface(context, style);
	}

	public Typeface getDefaultTypeface(Context context, Typeface originalTypeface) {
		int style = originalTypeface != null ? originalTypeface.getStyle() : Typeface.NORMAL;
		return getDefaultTypeface(context, style);
	}

	public Typeface getCustomTypeface(Context context, int style) {
		String fontType = FontTypes.REGULAR; // default Regular Pro font
		switch (style) {
			case Typeface.BOLD:
				fontType = FontTypes.BOLD;
				break;
			case Typeface.ITALIC:
				fontType = FontTypes.ITALIC;
				break;
			case Typeface.NORMAL:
				fontType = FontTypes.REGULAR;
				break;
			default:
				fontType = FontTypes.REGULAR;
				break;
		}
		Typeface face = getCustomTypeface(context, fontType);
		if(face == null){
			face = getDefaultTypeface(context, style);
		}
		return face;
	}

	public Typeface getDefaultTypeface(Context context, int style) {
		String fontType = FontTypes.REGULAR; // default Regular Pro font
		switch (style) {
			case Typeface.BOLD:
				fontType = FontTypes.BOLD;
				break;
			case Typeface.ITALIC:
				fontType = FontTypes.ITALIC;
				break;
			case Typeface.NORMAL:
				fontType = FontTypes.REGULAR;
				break;
			default:
				fontType = FontTypes.REGULAR;
				break;
		}
		Typeface face = getDefaultTypeface(context, fontType);
		if(face == null){
			face = Typeface.defaultFromStyle(style);
		}
		return face;
	}
}

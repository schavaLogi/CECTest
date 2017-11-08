package com.logitech.lip.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by nkumar3 on 6/24/2016.
 */

public class CustomViewMatchers {

    private CustomViewMatchers() {}

    public static Matcher withBackground(final Drawable drawable) {
       return new BoundedMatcher<View, ImageView>(ImageView.class) {

           @Override
           protected boolean matchesSafely(ImageView imageView) {
               /** Create a bitmap from the expected drawable */
               Bitmap expectedImageBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
               /** Get the drawable from the image view */
               Drawable displayedDrawable = imageView.getBackground();
               /** Create a bitmap the displayed drawable */
               Bitmap displayedImageBitmap = Bitmap.createBitmap(displayedDrawable.getIntrinsicWidth(), displayedDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
               /** Compare both and return the result */
               return expectedImageBitmap.sameAs(displayedImageBitmap);
           }

           @Override
           public void describeTo(Description description) {
               description.appendText("is image the same as: ");
               description.appendValue(drawable);
           }
       };
    }

    public static Matcher withDrawable(final Drawable drawable) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {

            @Override
            protected boolean matchesSafely(ImageView imageView) {
                /** Create a bitmap from the expected drawable */
                Bitmap expectedImageBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                /** Get the drawable from the image view */
                Drawable displayedDrawable = imageView.getDrawable();
                /** Create a bitmap the displayed drawable */
                Bitmap displayedImageBitmap = Bitmap.createBitmap(displayedDrawable.getIntrinsicWidth(), displayedDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                /** Compare both and return the result */
                return expectedImageBitmap.sameAs(displayedImageBitmap);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is image the same as: ");
                description.appendValue(drawable);
            }
        };
    }
}

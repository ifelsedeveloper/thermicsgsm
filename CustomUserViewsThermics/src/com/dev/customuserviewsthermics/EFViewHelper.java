package com.dev.customuserviewsthermics;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;


public class EFViewHelper {

    // set background drawable
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        }
        else {
            view.setBackground(drawable);
        }
    }

    public static void setBackgroundDrawableStates(View view, Drawable drawable, Drawable pressedDrawable) {
        setBackgroundDrawableStates(view, drawable, pressedDrawable, null);
    }


    public static void setBackgroundDrawableStates(View view, Drawable drawable, Drawable pressedDrawable, Drawable disabledDrawable) {
        StateListDrawable stateDrawable = makeStateDrawable(drawable, pressedDrawable, disabledDrawable);
        if (stateDrawable != null) {
            setBackgroundDrawable(view, stateDrawable);
        }
    }


    // make state drawable
    public static StateListDrawable makeStateDrawable(Drawable drawable, Drawable pressedDrawable) {
        return makeStateDrawable(drawable, pressedDrawable, null);
    }

    public static StateListDrawable makeStateDrawable(Drawable drawable, Drawable pressedDrawable, Drawable disabledDrawable) {
        boolean set = false;
        StateListDrawable stateDrawable = new StateListDrawable();
        if (disabledDrawable != null) {
            set = true;
            stateDrawable.addState(new int[] { -android.R.attr.state_enabled }, disabledDrawable);
        }
        if (pressedDrawable != null) {
            set = true;
            stateDrawable.addState(new int[] { android.R.attr.state_pressed }, pressedDrawable);
        }
        if (drawable != null) {
            set = true;
            stateDrawable.addState(new int[0], drawable);
        }
        return set ? stateDrawable : null;
    }



}
package com.blueeagle.flashlight;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

public class FontHelper {

    public static void changeFont(ViewGroup viewGroup, Typeface font) {

        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                changeFont((ViewGroup) view, font);
            } else if (view instanceof RadioButton) {
                ((RadioButton) view).setTypeface(font);
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(font);
            }
        }
    }
}

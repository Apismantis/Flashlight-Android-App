package com.blueeagle.flashlight;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreManager {
    private final String SHARE_PRE_NAME = "Flashlight-BlueEagle";
    private final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    private final String DONT_REMIND_AGAIN = "DONT_REMIND_AGAIN";
    private final String BRIGHTNESS_VALUE = "BRIGHTNESS_VALUE";
    private SharedPreferences sharePre;
    private SharedPreferences.Editor editor;

    public SharePreManager(Context context) {
        sharePre = context.getSharedPreferences(SHARE_PRE_NAME, 0);
        editor = sharePre.edit();
    }

    public boolean isFirstLaunch() {
        if (sharePre.contains(IS_FIRST_LAUNCH))
            return sharePre.getBoolean(IS_FIRST_LAUNCH, false);

        return true;
    }

    public void setRemindValue(int value) {
        editor.putInt(DONT_REMIND_AGAIN, value);
        editor.apply();
    }

    public int getRemindValue() {
        return sharePre.getInt(DONT_REMIND_AGAIN, 0);
    }

    public boolean isDontRemind() {
        return getRemindValue() == 2;
    }

    public float getBrightnessValue() {
        return sharePre.getFloat(BRIGHTNESS_VALUE, 1f);
    }

    public void saveBrightness(float value) {
        editor.putFloat(BRIGHTNESS_VALUE, value);
        editor.apply();
    }
}

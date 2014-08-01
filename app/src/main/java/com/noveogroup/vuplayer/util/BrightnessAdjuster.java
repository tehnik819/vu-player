/*******************************************************************************
 * Copyright © 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.util;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class BrightnessAdjuster {

    public final static String ERROR_TAG = "VuPlayer.ERROR_BRIGHTNESS_ADJUSTER";
    public final static String DEBUG_TAG = "VuPlayer.DEBUG_BRIGHTNESS_ADJUSTER";

    public final static float BRIGHTNESS_MIN = 0.02f;

    private static int savedBrightness;
    private static int savedBrightnessMode;

    private BrightnessAdjuster() {
        throw new UnsupportedOperationException("BrightnessAdjuster instance can not be created.");
    }

    public static void setManualMode(ContentResolver contentResolver) {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                               Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void saveSystemSettings(ContentResolver contentResolver) {
        try {
            savedBrightnessMode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException exception) {
            Log.e(ERROR_TAG, "Can not access system brightness mode.");
        }

        savedBrightness = getSystemBrightnessInt(contentResolver);
    }

    public static float setBrightness(ContentResolver contentResolver,
                                           Window window, float newBrightness) {
        if(newBrightness < 0 || newBrightness > 1) {
            return -1;
        }
        float newBrightnessReal = (1 - BRIGHTNESS_MIN) * newBrightness + BRIGHTNESS_MIN;
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS,
                                                              Math.round(255 * newBrightnessReal));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = newBrightnessReal;
        window.setAttributes(layoutParams);
        return newBrightness;
    }

    private static int getSystemBrightnessInt(ContentResolver contentResolver) {
        int brightness = 0;

        try {
            brightness = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException exception) {
            Log.e(ERROR_TAG, "Can not access system brightness.");
        }

        return brightness;
    }

    public static void restoreSavedSettings(ContentResolver contentResolver, Window window) {
        setBrightness(contentResolver, window, savedBrightness);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                               savedBrightnessMode);
    }

    public static float addBrightness(ContentResolver contentResolver, Window window,
                                                                                 float addition) {
        float brightness = (getSystemBrightnessInt(contentResolver) / (float) 255 - BRIGHTNESS_MIN)
                            / (1 - BRIGHTNESS_MIN);
//        Log.d(DEBUG_TAG, String.format("%f", brightness));
        if (addition > 0) {
            brightness = brightness + addition > 1 ? 1 : brightness + addition;
        } else {
            brightness = brightness + addition < 0 ? 0 : brightness + addition;
        }

        return setBrightness(contentResolver, window, brightness);
    }
}

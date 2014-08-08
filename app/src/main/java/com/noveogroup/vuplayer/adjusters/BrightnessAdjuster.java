/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.adjusters;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class BrightnessAdjuster {

    public final static String ERROR_TAG = "VuPlayer.ERROR_BRIGHTNESS_ADJUSTER";
    public final static String DEBUG_TAG = "VuPlayer.DEBUG_BRIGHTNESS_ADJUSTER";

    public final static float BRIGHTNESS_MIN = 0.01f;

    private static BrightnessAdjuster brightnessAdjuster = null;
    private int savedBrightness;
    private int savedBrightnessMode;
    private ContentResolver contentResolver;

    private BrightnessAdjuster(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public static BrightnessAdjuster getInstance(ContentResolver contentResolver) {
            return brightnessAdjuster = brightnessAdjuster == null ?
                                      new BrightnessAdjuster(contentResolver) : brightnessAdjuster;
    }

    public void setManualMode() {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                               Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public void saveSystemSettings() {
        try {
            savedBrightnessMode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException exception) {
            Log.e(ERROR_TAG, "Can not access system brightness mode.");
        }

        savedBrightness = getSystemBrightnessInt();
    }

    public float setBrightness(Window window, float newBrightness) {
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

    private int getSystemBrightnessInt() {
        int brightness = 0;

        try {
            brightness = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException exception) {
            Log.e(ERROR_TAG, "Can not access system brightness.");
        }

        return brightness;
    }

    public void restoreSavedSettings() {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, savedBrightness);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                               savedBrightnessMode);
    }

    public float addBrightness(Window window, float addition) {
        float brightness = (getSystemBrightnessInt() / (float) 255 - BRIGHTNESS_MIN)
                            / (1 - BRIGHTNESS_MIN);
//        Log.d(DEBUG_TAG, String.format("%f", brightness));
        if (addition > 0) {
            brightness = brightness + addition > 1 ? 1 : brightness + addition;
        } else {
            brightness = brightness + addition < 0 ? 0 : brightness + addition;
        }

        return setBrightness(window, brightness);
    }
}

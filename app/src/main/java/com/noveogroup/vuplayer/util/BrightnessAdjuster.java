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

    public final static int BRIGHTNESS_MIN = 25;

    private static int savedBrightness;
    private static int savedBrightnessMode;

    private BrightnessAdjuster() {
        throw new UnsupportedOperationException("BrightnessAdjuster instance can not be created.");
    }

    public static void setManualMode(ContentResolver contentResolver) {
        try {
            savedBrightnessMode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException exception) {
            Log.e(ERROR_TAG, "Can not access system brightness mode.");
        }
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                               Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void saveSystemBrightness(ContentResolver contentResolver) {
        savedBrightness = getSystemBrightness(contentResolver);
    }

    public static void setBrightness(ContentResolver contentResolver,
                                           Window window, int newBrightness) {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, newBrightness);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = newBrightness / (float) 255;
        window.setAttributes(layoutParams);
    }

    public static int getSystemBrightness(ContentResolver contentResolver) {
        int brightness = 0;

        try {
            brightness = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException exception) {
            Log.e(ERROR_TAG, "Can not access system brightness.");
        }

        return brightness;
    }

    public static void restoreSavedBrightness(ContentResolver contentResolver, Window window) {
        setBrightness(contentResolver, window, savedBrightness);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                               savedBrightnessMode);
    }

    public static int addBrightness(ContentResolver contentResolver, Window window, int addition) {
        int brightness = getSystemBrightness(contentResolver);
        Log.d(DEBUG_TAG, String.format("%d", brightness));
        if (addition > 0) {
            addition = brightness + addition > 255 ? 0 : addition;
        } else {
            addition = brightness + addition < BRIGHTNESS_MIN ? 0 : addition;
        }
        setBrightness(contentResolver, window, brightness + addition);
        return addition;
    }
}

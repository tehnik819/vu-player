/*******************************************************************************
 * Copyright © 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.listener;

import android.content.Context;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.noveogroup.vuplayer.ScreenAction;

public final class ScreenGestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final String DEBUG_TAG = "VuPlayer.DEBUG_SCREEN_GESTURE_LISTENER";
    public static final float SCROLL_MAX_COSINE = 0.95f;

    private OnScreenActionListener onScreenActionListener;

    public interface OnScreenActionListener {
        void performAction(ScreenAction screenAction, float distance);
    }

    public ScreenGestureListener(OnScreenActionListener onScreenActionListener) {
        this.onScreenActionListener = onScreenActionListener;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }



    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        onScreenActionListener.performAction(ScreenAction.SWITCH_ON_SINGLE_TAP, 0);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2,
                            float distanceX, float distanceY) {

        float deltaX = event2.getX() - event1.getX();
        float deltaY = event2.getY() - event1.getY();

        float scrollHorizontalCosine = (deltaX) / FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY);
        if(scrollHorizontalCosine > SCROLL_MAX_COSINE) {
            onScreenActionListener.performAction(ScreenAction.SEEK_FORWARD, deltaX);
            return true;
        }
        if(-scrollHorizontalCosine > SCROLL_MAX_COSINE) {
            onScreenActionListener.performAction(ScreenAction.SEEK_BACKWARD, -deltaX);
            return true;
        }

        float scrollVerticalCosine = (deltaY) / FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY);
        if(scrollVerticalCosine > SCROLL_MAX_COSINE) {
            int screen_width = getScreenWidth();
            if(event1.getX() < ((float) screen_width) / 3) {
                onScreenActionListener.performAction(ScreenAction.BRIGHTNESS_DOWN, deltaY);
                return true;
            }
            if(event1.getX() < ((float) screen_width) / 3 * 2) {
                onScreenActionListener.performAction(ScreenAction.SUBTITLES_DOWN, deltaY);
                return true;
            }
            onScreenActionListener.performAction(ScreenAction.VOLUME_DOWN, deltaY);
            return true;
        }

        if(-scrollVerticalCosine > SCROLL_MAX_COSINE) {
            int screen_width = getScreenWidth();
            if(event1.getX() < ((float) screen_width) / 3) {
                onScreenActionListener.performAction(ScreenAction.BRIGHTNESS_UP, deltaY);
                return true;
            }
            if(event1.getX() < ((float) screen_width) / 3 * 2) {
                onScreenActionListener.performAction(ScreenAction.SUBTITLES_UP, deltaY);
                return true;
            }
            onScreenActionListener.performAction(ScreenAction.VOLUME_UP, deltaY);
            return true;
        }

        return true;
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) ((Context) onScreenActionListener)
                                                       .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        return display.getWidth();
    }
}

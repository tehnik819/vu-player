/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.listener;

import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.noveogroup.vuplayer.enumeration.ScreenAction;

public final class OnScreenGestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final String DEBUG_TAG = "VuPlayer.DEBUG_SCREEN_GESTURE_LISTENER";
    public static final float SCROLL_MAX_COSINE = 0.95f;

    private OnScreenActionListener onScreenActionListener;
    private int width;

    public interface OnScreenActionListener {
        void performAction(ScreenAction screenAction, float distance);
    }

    public OnScreenGestureListener(OnScreenActionListener onScreenActionListener, int width) {
        this.onScreenActionListener = onScreenActionListener;
        this.width = width;
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

        float horizontalCosineFromStart = (deltaX) / FloatMath.sqrt(deltaX * deltaX
                                                                    + deltaY * deltaY);
        float horizontalCosine = -distanceX / FloatMath.sqrt(distanceX * distanceX
                                                             + distanceY * distanceY);

        if (horizontalCosineFromStart > SCROLL_MAX_COSINE
                                               || -horizontalCosineFromStart > SCROLL_MAX_COSINE) {
            if (horizontalCosine > SCROLL_MAX_COSINE || -horizontalCosine > SCROLL_MAX_COSINE) {
                onScreenActionListener.performAction(ScreenAction.SEEK_TO_ACTION, -distanceX);
            }
            return true;
        }

        float verticalCosineFromStart = (deltaY) / FloatMath.sqrt(deltaX * deltaX
                                                                  + deltaY * deltaY);
        float verticalCosine = -distanceY / FloatMath.sqrt(distanceX * distanceX
                                                           + distanceY * distanceY);

        if(verticalCosineFromStart > SCROLL_MAX_COSINE
                                                 || -verticalCosineFromStart > SCROLL_MAX_COSINE) {
            if (verticalCosine > SCROLL_MAX_COSINE || -verticalCosine > SCROLL_MAX_COSINE) {
                if (event2.getX() < ((float) width) / 3) {
                    onScreenActionListener.performAction(ScreenAction.BRIGHTNESS_CHANGE, distanceY);
                    return true;
                }
                if (event2.getX() < ((float) width) / 3 * 2) {
                    onScreenActionListener.performAction(ScreenAction.SUBTITLES_CHANGE, distanceY);
                    return true;
                }
                onScreenActionListener.performAction(ScreenAction.VOLUME_CHANGE, distanceY);
            }
        }

        return true;
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.listener;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;

public class OnScreenTouchListener implements View.OnTouchListener {

    private GestureDetectorCompat gestureDetectorCompat;

    public OnScreenTouchListener(Context context,
                OnScreenGestureListener.OnScreenActionListener onScreenActionListener, int width) {

        gestureDetectorCompat = new GestureDetectorCompat(context,
                                       new OnScreenGestureListener(onScreenActionListener, width));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetectorCompat.onTouchEvent(motionEvent);
    }
}

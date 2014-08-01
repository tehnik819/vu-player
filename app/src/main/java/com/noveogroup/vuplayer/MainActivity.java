/*******************************************************************************
 * Copyright © 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.noveogroup.vuplayer.listener.ScreenGestureListener;
import com.noveogroup.vuplayer.util.BrightnessAdjuster;

public class MainActivity extends ActionBarActivity
                          implements ScreenGestureListener.OnScreenActionListener {

    public final static String DEBUG_TAG = "VuPlayer.DEBUG_MAIN_ACTIVITY";

    public final static float BAR_LENGTH_IN_INCHES = 1.5f;

    private GestureDetectorCompat gestureDetectorCompat;
    private VideoPlayer videoPlayer;
    private TextView screenActionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new Library())
                .commit();

//        Create GestureDetectorCompat for the Activity.
        gestureDetectorCompat = new GestureDetectorCompat(this,
                                                new ScreenGestureListener(this, getScreenWidth()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    Override onTouchEvent() in order to use created GestureDetectorCompat.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(screenActionTextView != null) {
                screenActionTextView.setVisibility(View.INVISIBLE);
            }
        }
        else {
            videoPlayer = videoPlayer == null ? (VideoPlayer) findViewById(R.id.video_player)
                    : videoPlayer;
            if (videoPlayer != null && videoPlayer.getVisibility() == View.VISIBLE) {
                gestureDetectorCompat.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

//    Override method from OnScreenGestureListener.
    @Override
    public void performAction(ScreenAction screenAction, float distance) {
//        Log.d(DEBUG_TAG, screenAction.toString());
        switch (screenAction) {
            case BRIGHTNESS_CHANGE:
                float distanceRatio = getInches(distance, false) / BAR_LENGTH_IN_INCHES;
                float brightness = BrightnessAdjuster.addBrightness(getContentResolver(),
                        getWindow(), distanceRatio);
                showScreenActionMessage(String.format("Brightness: %d%%",
                        Math.round(brightness * 100)));
                break;
            default:
                showScreenActionMessage(screenAction.toString());
        }

    }

    private void showScreenActionMessage(String message) {
//        Show ScreenAction on the screen.
        screenActionTextView = screenActionTextView == null
                ? (TextView) findViewById(R.id.screen_action_text_view)
                : screenActionTextView;
        if(screenActionTextView != null) {
            screenActionTextView.setText(message);
            screenActionTextView.setVisibility(View.VISIBLE);
        }
    }

    private float getInches(float pixels, boolean isXAxis) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return isXAxis ? pixels / displayMetrics.xdpi : pixels / displayMetrics.ydpi;
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.subtitles.SubtitlesManager;
import com.noveogroup.vuplayer.subtitles.SubtitlesView;
import com.noveogroup.vuplayer.VideoController;
import com.noveogroup.vuplayer.VideoPlayer;
import com.noveogroup.vuplayer.adjusters.AudioAdjuster;
import com.noveogroup.vuplayer.adjusters.BrightnessAdjuster;
import com.noveogroup.vuplayer.enumerations.ScreenAction;
import com.noveogroup.vuplayer.listeners.OnScreenGestureListener;
import com.noveogroup.vuplayer.listeners.OnScreenTouchListener;
import com.noveogroup.vuplayer.utils.TimeConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class VideoFragment extends Fragment
                           implements OnScreenGestureListener.OnScreenActionListener {

    private static final String KEY_CURRENT_POSITION = "com.noveogroup.vuplayer.current_position";
    private static final String KEY_CURRENT_STATE = "com.noveogroup.vuplayer.current_state";
    private final static String TAG = "VideoFragment";
//    public final static int SUBTITLES_CHECK_DELAY = 100;

    private int seekTime;
    private Properties properties;
    private String viewSource;
    private BrightnessAdjuster brightnessAdjuster;
    private AudioAdjuster audioAdjuster;
    private int hScrollBarStepPixels;
    private int vScrollBarLengthPixels;

    private SubtitlesManager subtitlesManager;
    private VideoPlayer videoPlayer;
    private TextView screenActionTextView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        initProperties();
        View view = inflater.inflate(R.layout.fragment_video, container, false);

//        Set up brightness control.
        brightnessAdjuster = BrightnessAdjuster.getInstance(getActivity().getContentResolver());
        brightnessAdjuster.saveSystemSettings();
        brightnessAdjuster.setManualMode();
//        Set up AudioAdjuster.
        audioAdjuster = AudioAdjuster.getInstance((AudioManager) getActivity()
                                                  .getSystemService(Context.AUDIO_SERVICE));
//        Get volume and brightness scroll bars length in pixels.
        hScrollBarStepPixels = getResources().getDimensionPixelSize(R.dimen.h_scroll_bar_step);
//        Get seek scroll bar step in pixels.
        vScrollBarLengthPixels = getResources().getDimensionPixelSize(R.dimen.v_scroll_bar_length);

        view.findViewById(R.id.fragment_video).setOnTouchListener(
                                new OnScreenTouchListener(getActivity(), this, getScreenWidth()) {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    screenActionTextView = screenActionTextView == null
                            ? (TextView) getActivity().findViewById(R.id.screen_action_text_view)
                            : screenActionTextView;
                    if(screenActionTextView != null) {
                        super.onTouch(view, motionEvent);
                        screenActionTextView.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }

                return super.onTouch(view, motionEvent);
            }
        });

//        Initialize videoPlayer.
        videoPlayer = (VideoPlayer) view.findViewById(R.id.video_player);
        videoPlayer.setVideoController((VideoController) view.findViewById(R.id.video_controller));
        videoPlayer.setDataSource(viewSource);
        videoPlayer.setSeekTime(seekTime);
        videoPlayer.prepare();

//        Initialize subtitles display.
        subtitlesManager = new SubtitlesManager(videoPlayer,
                                           (SubtitlesView) view.findViewById(R.id.subtitles_view));
        subtitlesManager.loadSubtitles(viewSource);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            videoPlayer.seekTo(savedInstanceState.getInt(KEY_CURRENT_POSITION));
            videoPlayer.handleState(savedInstanceState.getInt(KEY_CURRENT_STATE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, videoPlayer.getCurrentPosition());
        outState.putInt(KEY_CURRENT_STATE, videoPlayer.getCurrentState());
    }

    private void initProperties() {
        properties = new Properties();
        try {
            InputStream rawResources = getResources().openRawResource(R.raw.config);
            properties.load(rawResources);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(),e);
        }
        seekTime = Integer.valueOf(properties.getProperty("seek_time", String.valueOf(5000)));
        viewSource = Environment.getExternalStorageDirectory().toString()
                + getResources().getString(R.string.filename);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        videoPlayer.release();
        videoPlayer = null;
        screenActionTextView = null;
        subtitlesManager.releaseViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause");

        brightnessAdjuster.restoreSavedSettings();
        subtitlesManager.stopSubtitling();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoPlayer.play();
        subtitlesManager.runSubtitling();
    }

    //    Override the method from OnScreenGestureListener.
    @Override
    public void performAction(ScreenAction screenAction, float distance) {
        switch (screenAction) {
            case SWITCH_ON_SINGLE_TAP:
                videoPlayer.changeControllerVisibility();
            case BRIGHTNESS_CHANGE:
                float distanceRatio = distance / vScrollBarLengthPixels;
                float brightness = brightnessAdjuster.addBrightness(getActivity().getWindow(), distanceRatio);
                showScreenActionMessage(String.format("Brightness: %d%%",
                        Math.round(brightness * 100)));
                break;
            case VOLUME_CHANGE:
                distanceRatio = distance / vScrollBarLengthPixels;
                float volume = audioAdjuster.addVolume(distanceRatio);
                showScreenActionMessage(String.format("Volume: %d%%",
                        Math.round(volume * 100)));
                break;
            case SEEK_TO_ACTION:
                int currentPosition = videoPlayer.addTime((int) (distance / hScrollBarStepPixels * 1000));
                int duration = videoPlayer.getDuration();
                showScreenActionMessage(TimeConverter.convertToString(currentPosition, duration));
                break;
            default:
                showScreenActionMessage(screenAction.toString());
        }
    }

    private void showScreenActionMessage(String message) {
        screenActionTextView = screenActionTextView == null
                ? (TextView) getActivity().findViewById(R.id.screen_action_text_view)
                : screenActionTextView;
        if(screenActionTextView != null) {
            screenActionTextView.setText(message);
            screenActionTextView.setVisibility(View.VISIBLE);
        }
    }

        private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }
}

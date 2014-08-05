/*******************************************************************************
 * Copyright © 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.SubtitlesView;
import com.noveogroup.vuplayer.VideoController;
import com.noveogroup.vuplayer.VideoPlayer;
import com.noveogroup.vuplayer.adjuster.AudioAdjuster;
import com.noveogroup.vuplayer.adjuster.BrightnessAdjuster;
import com.noveogroup.vuplayer.enumeration.ScreenAction;
import com.noveogroup.vuplayer.listener.OnScreenGestureListener;
import com.noveogroup.vuplayer.listener.OnScreenTouchListener;
import com.noveogroup.vuplayer.util.TimeConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.LogRecord;

import subtitleFile.Caption;
import subtitleFile.FormatSRT;
import subtitleFile.TimedTextObject;


public class VideoFragment extends Fragment
                           implements OnScreenGestureListener.OnScreenActionListener {

    private static final String KEY_CURRENT_POSITION = "com.noveogroup.vuplayer.current_position";
    private static final String KEY_CURRENT_STATE = "com.noveogroup.vuplayer.current_state";
    private final static String TAG = "VideoFragment";
    public final static int SUBTITLES_CHECK_DELAY = 100;

    private int seekTime;
    private Properties properties;
    private String viewSource;
    private BrightnessAdjuster brightnessAdjuster;
    private AudioAdjuster audioAdjuster;
    private int hScrollBarStepPixels;
    private int vScrollBarLengthPixels;
    private Handler handler;
    private Runnable subtitlesRunnable;
    TimedTextObject subtitlesTextObject;

    private VideoPlayer videoPlayer;
    private TextView screenActionTextView;
    private SubtitlesView subtitlesView;



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

//        Initialize subtitles display.
        String srtFilename = viewSource.replace(".mp4", ".srt");
        File subtitles_file = new File(srtFilename);

        try {
            FileInputStream stream = new FileInputStream(subtitles_file);
            subtitlesTextObject = new FormatSRT().parseFile(srtFilename, stream);
            stream.close();

            Log.d(TAG, String.format("File: %s", subtitlesTextObject.fileName));
            Log.d(TAG, String.format("Size: %d", subtitlesTextObject.captions.values().size()));

        } catch (IOException exception) {
            Log.e(TAG, "Can not read subtitles file.");
        }

        subtitlesView = (SubtitlesView) view.findViewById(R.id.subtitles_view);
        subtitlesView.setText("Ok.");


//        Run videoPlayer.
        videoPlayer.prepare();
        videoPlayer.play();
        runSubtitlesLoop();

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
        subtitlesView = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause");

//        Restore saved system brightness settings.
        brightnessAdjuster.restoreSavedSettings();
        handler.removeCallbacks(subtitlesRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(subtitlesRunnable);
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

    private void runSubtitlesLoop() {
        handler = new Handler();

        subtitlesRunnable = new Runnable() {
            @Override
            public void run() {
                if(videoPlayer.isPlaying()) {
                    int currentPosition = videoPlayer.getCurrentPosition();
                    Collection<Caption> subtitles =  subtitlesTextObject.captions.values();
                    for(Caption caption : subtitles) {
                        if(currentPosition >= caption.start.getMilliseconds()
                                && currentPosition <= caption.end.getMilliseconds()) {
                            subtitlesView.setText(Html.fromHtml(caption.content));
                            subtitlesView.setClickable(true);
                            return;
                        }
                    }
                    subtitlesView.setText("");
                    subtitlesView.setClickable(false);
                }
            }
        };

        new Thread() {
            @Override
            public void run() {
                while(true) {
                    handler.post(subtitlesRunnable);
                    try {
                        sleep(SUBTITLES_CHECK_DELAY);
                    } catch (InterruptedException exception) {
                        Log.e(TAG, "Subtitles thread has been interrupted.");
                    }
                }
            }
        }.start();
    }
}

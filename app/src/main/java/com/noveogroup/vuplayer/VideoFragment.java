package com.noveogroup.vuplayer;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VideoFragment extends Fragment {
    private int seekTime;
    private Properties properties;
    private String viewSource;

    private VideoPlayer videoPlayer;
    private VideoController videoController;

    private static final String KEY_CURRENT_POSITION = "com.noveogroup.vuplayer.current_position";
    private static final String KEY_CURRENT_STATE = "com.noveogroup.vuplayer.current_state";
    private final static String TAG = "VideoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        initProperties();
        View v = inflater.inflate(R.layout.fragment_video, container, false);

        videoPlayer = (VideoPlayer) v.findViewById(R.id.video_player);
        videoController = (VideoController) v.findViewById(R.id.video_controller);

        videoPlayer.setVideoController(videoController);
        videoPlayer.setDataSource(viewSource);
        videoPlayer.setSeekTime(seekTime);
        videoPlayer.prepare();
        videoPlayer.play();

        return v;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
    }

    private void initProperties() {
        properties = new Properties();
        try {
            InputStream rawResources = getResources().openRawResource(R.raw.config);
            properties.load(rawResources);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(),e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        seekTime = Integer.valueOf(properties.getProperty("seek_time", String.valueOf(5000)));
        viewSource = Environment.getExternalStorageDirectory().toString() + "/test.mp4";
    }
}

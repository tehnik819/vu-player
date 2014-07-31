/*******************************************************************************
 * Copyright © 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

    private final static String TAG = "VideoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        videoPlayer = null;
        videoController = null;
    }
}

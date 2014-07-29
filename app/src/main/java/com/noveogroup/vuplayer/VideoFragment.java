package com.noveogroup.vuplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback {
    private int seekTime;
    private Properties properties;
    private MediaPlayer mediaPlayer;
    private String viewSource;
    private Button playBtn;
    private Button pauseBtn;
    private Button backwardBtn;
    private Button forwardBtn;
    private SurfaceView videoSurface;
    private final static String TAG = "VideoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initProperties();
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        viewSource = Environment.getExternalStorageDirectory().toString() + "/test.mp4";
        mediaPlayer = new MediaPlayer();

        playBtn = (Button) v.findViewById(R.id.play);
        pauseBtn = (Button) v.findViewById(R.id.pause);
        backwardBtn = (Button) v.findViewById(R.id.backward);
        forwardBtn = (Button) v.findViewById(R.id.forward);

        videoSurface = (SurfaceView) v.findViewById(R.id.video_surface);
        SurfaceHolder holder = videoSurface.getHolder();
        holder.addCallback(this);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });

        backwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - seekTime);
            }
        });

        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + seekTime);
            }
        });

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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setDataSource(viewSource);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaPlayer.release();
    }
}

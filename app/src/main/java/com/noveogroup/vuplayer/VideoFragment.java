package com.noveogroup.vuplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback {
    public final static int seekTime = 3000;
    //private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private FragmentActivity mainContext;
    private String viewSource;
    private Button playBtn;
    private Button pauseBtn;
    private Button backwardBtn;
    private Button forwardBtn;
    private SurfaceView videoSurface;

    @Override
    public void onAttach(Activity activity) {
        mainContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        Log.d("VideoFragment", Environment.getExternalStorageDirectory().toString());
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
                if(mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });

        backwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - seekTime);
                }
            }
        });

        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + seekTime);
                }
            }
        });

        /*
        videoView = (VideoView) v.findViewById(R.id.videoView);
        videoView.setVideoPath(viewSource);
        MediaController mc = new MediaController(mainContext);
        videoView.setMediaController(mc);
        videoView.requestFocus(0);
        videoView.start();
        */
        return v;
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

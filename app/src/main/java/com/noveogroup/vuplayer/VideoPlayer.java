package com.noveogroup.vuplayer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

public class VideoPlayer extends SurfaceView implements SurfaceHolder.Callback {
    public static final int REPEAT_MODE_NOT_REPEAT = 0;
    public static final int REPEAT_MODE_SINGLE_TRACK = 1;
    public static final int REPEAT_MODE_PLAYLIST = 2;

    private int repeatMode = REPEAT_MODE_NOT_REPEAT;
    private MediaPlayer mediaPlayer;
    private VideoController mVideoController;
    private int seekTime;
    private String mDataSource;

    private int mVideoWidth;
    private int mVideoHeight;

    private static final int BEGIN_OF_VIDEO_TIME = 0;
    private static final String TAG = "VideoPlayer";

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mediaPlayer = new MediaPlayer();
        getHolder().addCallback(this);
        setFocusable(true);
        requestFocus();
        setMediaPlayerListener();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mVideoController = null;
        mediaPlayer.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                if(mVideoController.isShowing()) {
                    Log.d(TAG, "Hide control");
                    mVideoController.hide();
                }
                else {
                    Log.d(TAG, "Show control");
                    mVideoController.show();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public void setDataSource(String source) {
        mDataSource = source;
        try {
            mediaPlayer.setDataSource(mDataSource);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        Log.d(TAG, "setDataSource() | VideoWidth = " + mediaPlayer.getVideoWidth() + "; VideoHeight = " + mediaPlayer.getVideoHeight());
    }

    public void setSeekTime(int seekTime) {
        this.seekTime = seekTime;
    }

    public void setRepeatMode(int mode) {
        repeatMode = mode;
    }

    public void setVideoController(VideoController vc) {
        mVideoController = vc;
        mVideoController.setVideoPlayer(this);
    }

    public void prepare() {
        try {
            mVideoWidth = mediaPlayer.getVideoWidth();
            mVideoHeight = mediaPlayer.getVideoHeight();
            getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            Log.d(TAG, "prepare() | VideoWidth = " + mediaPlayer.getVideoWidth() + "; VideoHeight = " + mediaPlayer.getVideoHeight());
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void play() {
        if(!mediaPlayer.isPlaying()) {
            Log.d(TAG, "play() | VideoWidth = " + mediaPlayer.getVideoWidth() + "; VideoHeight = " + mediaPlayer.getVideoHeight());
            mVideoHeight = mediaPlayer.getVideoHeight();
            mVideoWidth = mediaPlayer.getVideoWidth();
            fitToScreen();
            mediaPlayer.start();
            //mVideoController.hide();
            Log.d(TAG, "mVideoView.isShowing() = " + mVideoController.isShowing());
        }
    }

    public void pause() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void backward() {
        if(mediaPlayer.getCurrentPosition() >= seekTime) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - seekTime);
        }
        else if(mediaPlayer.getCurrentPosition() < seekTime) {
            mediaPlayer.seekTo(BEGIN_OF_VIDEO_TIME);
        }
    }

    public void forward() {
        if(mediaPlayer.getCurrentPosition() <= (mediaPlayer.getDuration() - seekTime)) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + seekTime);
        }
        else if(mediaPlayer.getCurrentPosition() > (mediaPlayer.getDuration() - seekTime)) {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void seekTo(int millis) {
        mediaPlayer.seekTo(millis);
    }

    private void setMediaPlayerListener() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
                Toast.makeText(getContext(), "Playing video is complete", Toast.LENGTH_LONG).show();
                switch (repeatMode) {
                    case REPEAT_MODE_NOT_REPEAT:
                        //next track or another
                        break;
                    case REPEAT_MODE_SINGLE_TRACK:
                        seekTo(BEGIN_OF_VIDEO_TIME);
                        mediaPlayer.start();
                        break;
                    case REPEAT_MODE_PLAYLIST:
                        //seek to begin of playlist
                        break;
                }
            }
        });
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    private void fitToScreen() {
        mVideoWidth = mediaPlayer.getVideoWidth();
        mVideoHeight = mediaPlayer.getVideoHeight();
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        double widthRatio = ((double)(displayMetrics.widthPixels))/mVideoWidth;
        double heightRatio = ((double)(displayMetrics.heightPixels))/mVideoHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                (int)(mVideoWidth*ratio), (int)(mVideoHeight*ratio));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        this.setLayoutParams(layoutParams);
    }
}

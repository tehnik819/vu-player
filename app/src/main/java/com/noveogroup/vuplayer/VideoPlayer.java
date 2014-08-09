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
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

public class VideoPlayer extends SurfaceView {
    public static final int REPEAT_MODE_NOT_REPEAT = 0;
    public static final int REPEAT_MODE_SINGLE_TRACK = 1;
    public static final int REPEAT_MODE_PLAYLIST = 2;

    private int repeatMode = REPEAT_MODE_NOT_REPEAT;
    private volatile MediaPlayer mediaPlayer;
    private VideoController mVideoController;
    private int seekTime;
    private String mDataSource;
    private SurfaceHolder mSurfaceHolder;

    private ProgressTask progressTask;

    private int mVideoWidth;
    private int mVideoHeight;

    private int currentState = STATE_IDLE;

    private static final int BEGIN_OF_VIDEO_TIME = 0;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PLAY = 1;
    public static final int STATE_STOP = 2;
    private static final String TAG = "VideoPlayer";

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "CONSTRUCTOR");
        mediaPlayer = new MediaPlayer();
        getHolder().addCallback(mSHCallback);
        setMediaPlayerListener();
        setFocusable(true);
        requestFocus();
    }

    private SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated()");
            mSurfaceHolder = holder;
            mediaPlayer.setDisplay(mSurfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged()");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed()");
            mSurfaceHolder = null;
            currentState = STATE_IDLE;
        }
    };

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
            Log.d(TAG, "prepare()");
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        mVideoController.setSeekBarMax(mediaPlayer.getDuration());
    }

    public void release() {
        pause();
        mSurfaceHolder = null;
        mVideoController = null;
        mediaPlayer.release();
        mediaPlayer = null;
        mDataSource = null;
    }

    public void play() {
        if(!mediaPlayer.isPlaying()) {
            Log.d(TAG, "play() | VideoWidth = " + mediaPlayer.getVideoWidth() + "; VideoHeight = " + mediaPlayer.getVideoHeight());
            mVideoHeight = mediaPlayer.getVideoHeight();
            mVideoWidth = mediaPlayer.getVideoWidth();
            fitToScreen();
            mediaPlayer.start();
            currentState = STATE_PLAY;
            mVideoController.updatePausePlay(currentState);

            if(progressTask == null || progressTask.isCancelled()) {
                progressTask = new ProgressTask();
                progressTask.link(this);
                progressTask.execute();
            }

        }
    }

    public void pause() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        currentState = STATE_STOP;
        mVideoController.updatePausePlay(currentState);

        if(progressTask != null) {
            progressTask.cancel(true);
            progressTask = null;
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
                currentState = STATE_STOP;
                mVideoController.trackComplete();
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

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentState() {
        return currentState;
    }

    public void handleState(int state) {
        Log.d(TAG, "handleState()");
        switch (state) {
            case STATE_IDLE:
                pause();
                break;
            case STATE_PLAY:
                play();
                break;
            case STATE_STOP:
                pause();
                break;
        }
        mVideoController.updatePausePlay(state);
    }

    public void updateTimeText(int currentMillis, int duration) {
        mVideoController.updateProgress(VideoController.timeToString(currentMillis), VideoController.timeToString(duration), currentMillis);
    }
}

package com.noveogroup.vuplayer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
    private MediaPlayer mediaPlayer;
    private TopBar mTopBar;
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
    public static final int STATE_SOUGHT = 3;
    public static final int STATE_UNDEFINED = 4;
    private static final String TAG = "VideoPlayer";

    private OnChangeStateListener onChangeStateListener;

    public interface OnChangeStateListener {
        void onChangeState(int state);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mediaPlayer = new MediaPlayer();
        getHolder().addCallback(mSHCallback);
        setMediaPlayerListener();
        setFocusable(true);
        requestFocus();
    }

    private SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            mediaPlayer.setDisplay(mSurfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            currentState = STATE_IDLE;
        }
    };

    public void setDataSource(String source) {
        mDataSource = source;
        try {
            mediaPlayer.setDataSource(mDataSource);
            mTopBar.setTitle(getSimpleFileName(mDataSource));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static String getSimpleFileName(String name) {
        int count = name.length() - 1;
        for(int i = name.length() - 1; i >= 0; i--) {
            if(name.charAt(i) == '/' || name.charAt(i) == '\\') {
                break;
            }
            count--;
        }
        return name.substring(count + 1, name.length());
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

    public void setTopBar(TopBar tb) {
        mTopBar = tb;
    }

    public void prepare() {
        try {
            mVideoWidth = mediaPlayer.getVideoWidth();
            mVideoHeight = mediaPlayer.getVideoHeight();
            getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        mVideoController.setSeekBarMax(mediaPlayer.getDuration());
    }

    public void release() {
        mSurfaceHolder = null;
        mVideoController = null;
        mediaPlayer.release();
        mediaPlayer = null;
        mDataSource = null;
    }

    public void play() {
        if(!mediaPlayer.isPlaying()) {
            mVideoHeight = mediaPlayer.getVideoHeight();
            mVideoWidth = mediaPlayer.getVideoWidth();
            fitToScreen();
            mediaPlayer.start();
            currentState = STATE_PLAY;
            if (onChangeStateListener != null) {
                onChangeStateListener.onChangeState(STATE_PLAY);
            }
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
        addTime(-seekTime);
    }

    public void forward() {
        addTime(seekTime);
    }

    public void seekTo(int millis) {
        mediaPlayer.seekTo(millis);
        if (onChangeStateListener != null) {
            onChangeStateListener.onChangeState(STATE_SOUGHT);
        }
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

    public int getCurrentState() {
        return currentState;
    }

    public void handleState(int state) {
        currentState = state;
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

    public void changeControllerVisibility() {
        if(mVideoController.isShowing()) {
            mVideoController.hide();
            mTopBar.hide();
        }
        else {
            mVideoController.show();
            mTopBar.show();
        }
    }

    public int addTime(final int addition) {
        int currentPosition = mediaPlayer.getCurrentPosition();

        if(addition > 0) {
            currentPosition = currentPosition + addition > mediaPlayer.getDuration()
                            ? mediaPlayer.getDuration() : currentPosition + addition;
        }
        else {
            currentPosition = currentPosition + addition < BEGIN_OF_VIDEO_TIME
                            ? BEGIN_OF_VIDEO_TIME : currentPosition + addition;
        }

        seekTo(currentPosition);

        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void setOnChangeStateListener(OnChangeStateListener listener) {
        onChangeStateListener = listener;
    }

    public void updateTimeText(int currentMillis, int duration) {
        mVideoController.updateProgress(VideoController.timeToString(currentMillis),
                VideoController.timeToString(duration), currentMillis);
    }
}

package com.noveogroup.vuplayer;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class VideoController extends RelativeLayout {

    private View mRoot;
    private Context mContext;
    private VideoPlayer mVideoPlayer;

    private ImageButton mPlayButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mCurrentTime;
    private TextView mDurationTime;

    private SeekBar mSeekBar;

    private Animation pullOut;
    private Animation pullAway;

    private boolean isShow = true;

    private static final String TAG = "VideoController";

    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRoot = getRoot();
        this.addView(mRoot);
        initControllerView(mRoot);
        initAnimations();
        setButtonsListeners();
    }

    public VideoController(Context context) {
        super(context);
        mContext = context;
        mRoot = getRoot();
        this.addView(mRoot);
        initControllerView(mRoot);
        initAnimations();
        setButtonsListeners();
    }

    private View getRoot() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(R.layout.controller, null);
    }

    private void initControllerView(View view) {
        mPlayButton = (ImageButton) view.findViewById(R.id.control_play_btn);
        mNextButton = (ImageButton) view.findViewById(R.id.control_next_btn);
        mPrevButton = (ImageButton) view.findViewById(R.id.control_prev_btn);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mCurrentTime = (TextView) view.findViewById(R.id.text_current_time);
        mDurationTime = (TextView) view.findViewById(R.id.text_duration);
    }

    private void initAnimations() {
        pullOut = AnimationUtils.loadAnimation(mContext, R.anim.pull_out);
        pullOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRoot.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        pullAway = AnimationUtils.loadAnimation(mContext, R.anim.pull_away);
        pullAway.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRoot.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void setButtonsListeners() {
        mPrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoPlayer.backward();
            }
        });

        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoPlayer.forward();
            }
        });

        mPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoPlayer.isPlaying()) {
                    mVideoPlayer.pause();
                }
                else {
                    mVideoPlayer.play();
                }
                updatePausePlay(mVideoPlayer.getCurrentState());
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mVideoPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setVideoPlayer(VideoPlayer vp) {
        mVideoPlayer = vp;
    }

    public void show() {
        this.startAnimation(pullOut);
        isShow = true;
    }

    public void hide() {
        this.startAnimation(pullAway);
        isShow = false;
    }

    public boolean isShowing() {
        return isShow;
    }

    public void updatePausePlay(int state) {
        switch (state) {
            case VideoPlayer.STATE_PLAY:
                mPlayButton.setImageResource(R.drawable.ic_pause);
                break;
            case VideoPlayer.STATE_STOP:
                mPlayButton.setImageResource(R.drawable.ic_play);
                break;
            case VideoPlayer.STATE_IDLE:
                mPlayButton.setImageResource(R.drawable.ic_play);
                break;
        }
    }

    public void setSeekBarMax(int max) {
        mSeekBar.setMax(max);
    }

    public void trackComplete() {
        updatePausePlay(VideoPlayer.STATE_STOP);
    }

    public static String timeToString(int millis) {
        int hours = (int)(millis/DateUtils.HOUR_IN_MILLIS);
        int minutes = (int)((millis - hours*DateUtils.HOUR_IN_MILLIS)/DateUtils.MINUTE_IN_MILLIS);
        int seconds = (int)((millis - hours*DateUtils.HOUR_IN_MILLIS - minutes*DateUtils.MINUTE_IN_MILLIS)/DateUtils.SECOND_IN_MILLIS);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void updateProgress(String currentText, String durationText, int seekProgress) {
        mCurrentTime.setText(currentText);
        mDurationTime.setText(durationText);
        mSeekBar.setProgress(seekProgress);
    }
}

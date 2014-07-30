package com.noveogroup.vuplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class VideoController extends RelativeLayout {

    private View mRoot;
    private Context mContext;
    private VideoPlayer mVideoPlayer;

    private ImageButton mPlayButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;

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
                    mPlayButton.setImageResource(R.drawable.ic_play);
                }
                else {
                    mVideoPlayer.play();
                    mPlayButton.setImageResource(R.drawable.ic_pause);
                }
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
}

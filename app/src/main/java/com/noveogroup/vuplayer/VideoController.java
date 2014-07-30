package com.noveogroup.vuplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class VideoController extends RelativeLayout{

    private View mRoot;
    private Context mContext;
    private VideoPlayer mVideoPlayer;

    private ImageButton mPlayButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;

    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRoot = getRoot();
        this.addView(mRoot);
        initControllerView(mRoot);
        setButtonsListeners();
    }

    public VideoController(Context context) {
        super(context);
        mContext = context;
        mRoot = getRoot();
        this.addView(mRoot);
        initControllerView(mRoot);
        setButtonsListeners();
    }

    private void addViewsToRoot() {


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


}

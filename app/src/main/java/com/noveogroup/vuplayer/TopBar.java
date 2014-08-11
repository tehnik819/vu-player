package com.noveogroup.vuplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBar extends RelativeLayout {
    private Context mContext;
    private View mRoot;
    private ImageButton mImageClose;
    private TextView mTitle;
    private TextView mBatteryText;
    private ProgressBar mBatteryBar;

    private Animation pullAway;
    private Animation pullOut;
    private boolean isShow = true;

    private OnBarClickListener listener;

    public interface OnBarClickListener {
        void onClick(View v);
    }

    public void setOnBarClickListener(OnBarClickListener l) {
        listener = l;
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRoot = getRoot();
        this.addView(mRoot);
        initBarView();
        initAnimations();
        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            int scale = -1;
            int level = -1;
            int voltage = -1;
            int temp = -1;
            @Override
            public void onReceive(Context context, Intent intent) {
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                updateBattery(level, scale);
                Log.e("BatteryManager", "level is " + level + "/" + scale + ", temp is " + temp + ", voltage is " + voltage);
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(batteryReceiver, filter);
    }

    private void initBarView() {
        mImageClose = (ImageButton) mRoot.findViewById(R.id.top_bar_close);
        mImageClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(mImageClose);
                }
            }
        });
        mTitle = (TextView) mRoot.findViewById(R.id.top_bar_title);
        mBatteryText = (TextView) mRoot.findViewById(R.id.top_bar_battery_text);
        mBatteryBar = (ProgressBar) mRoot.findViewById(R.id.top_bar_battery_bar);
        mBatteryBar.setProgressDrawable(getResources().getDrawable(R.drawable.vertical_progress_bar));
    }

    private View getRoot() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.top_bar, null);
    }

    private void initAnimations() {
        pullAway = AnimationUtils.loadAnimation(mContext, R.anim.top_bar_pull_away);
        pullAway.setAnimationListener(new Animation.AnimationListener() {
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

        pullOut = AnimationUtils.loadAnimation(mContext, R.anim.top_bar_pull_out);
        pullOut.setAnimationListener(new Animation.AnimationListener() {
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

    public void show() {
        this.startAnimation(pullAway);
        isShow = true;
    }

    public void hide() {
        this.startAnimation(pullOut);
        isShow = false;
    }

    public boolean isShowing() {
        return isShow;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    private void updateBattery(int level, int scale) {
        mBatteryText.setText(level + "%");
        mBatteryBar.setMax(scale);
        mBatteryBar.setProgress(level);
    }
}

package com.noveogroup.vuplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class ProgressTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "ProgressTask";
    private VideoPlayer videoPlayer;

    public void link(VideoPlayer vc) {
        videoPlayer = vc;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Task Executed!!!!!");
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, "Task Canceled!!!!!!!!");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(!isCancelled()) {
            videoPlayer.updateTimeText(values[0], videoPlayer.getDuration());
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        int currentPosition = videoPlayer.getCurrentPosition();
        int total = videoPlayer.getDuration();

        while (videoPlayer != null && currentPosition < total && !isCancelled()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                currentPosition = videoPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                cancel(true);
                Log.e(TAG, e.getMessage(), e);
                break;
            }
            publishProgress(currentPosition);
        }
        return null;
    }
}

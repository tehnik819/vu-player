/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.subtitles;

import android.os.Handler;
import android.text.Html;
import android.util.Log;

import com.noveogroup.vuplayer.VideoPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import subtitleFile.Caption;
import subtitleFile.FormatSRT;
import subtitleFile.TimedTextObject;

public class SubtitlesManager {

    private static final String TAG = "VuPlayer.SubtitlesManager";
    private static final int SUBTITLES_CHECK_DELAY = 100;

    private Handler handler = null;
    private Runnable subtitlingRunnable;
    private Thread subtitlingThread;
    private TimedTextObject subtitlesTextObject;

    private VideoPlayer videoPlayer;
    private SubtitlesView subtitlesView;


    public SubtitlesManager(VideoPlayer videoPlayer, SubtitlesView subtitlesView) {
        this.videoPlayer = videoPlayer;
        this.subtitlesView = subtitlesView;
    }

    public void loadSubtitles(String videoFilename) {

//        String[] subtitlesFilenames = FileManager.getFilesByStart()
        String srtFilename = videoFilename.replace(".mp4", ".srt");
        System.out.println(srtFilename);
        File subtitlesFile = new File(srtFilename);

        try {
            FileInputStream stream = new FileInputStream(subtitlesFile);
            Log.e(TAG, "Trying to load...");
            subtitlesTextObject = SubtitlesLoader.parseFile(srtFilename, stream);
            stream.close();

            Log.d(TAG, String.format("File: %s", subtitlesTextObject.fileName));
            Log.d(TAG, String.format("Size: %d", subtitlesTextObject.captions.values().size()));

        } catch (Exception exception) {
            Log.e(TAG, "Can not load subtitles file.");
        }
    }

    public void runSubtitling() {
        handler = new Handler();

        subtitlingRunnable = new Runnable() {
            @Override
            public void run() {
                if(videoPlayer.isPlaying()) {
                    int currentPosition = videoPlayer.getCurrentPosition();
                    Collection<Caption> subtitles =  subtitlesTextObject.captions.values();
                    for(Caption caption : subtitles) {
                        if(currentPosition >= caption.start.getMilliseconds()
                                && currentPosition <= caption.end.getMilliseconds()) {
                            subtitlesView.setText(Html.fromHtml(caption.content));
                            subtitlesView.setClickable(true);
                            return;
                        }
                    }
                    subtitlesView.setText("");
                    subtitlesView.setClickable(false);
                }
            }
        };

        subtitlingThread = new Thread() {
            @Override
            public void run() {
                while(true) {
                    if (isInterrupted()) {
                        return;
                    }
                    handler.post(subtitlingRunnable);
                    try {
                        sleep(SUBTITLES_CHECK_DELAY);
                    } catch (InterruptedException exception) {
                        Log.e(TAG, "Subtitles thread has been interrupted.");
                    }
                }
            }
        };
        subtitlingThread.start();
    }

    public void releaseViews() {
        videoPlayer = null;
        subtitlesView = null;
    }

    public void stopSubtitling() {
        handler.removeCallbacks(subtitlingRunnable);
        subtitlingThread.interrupt();
    }
}

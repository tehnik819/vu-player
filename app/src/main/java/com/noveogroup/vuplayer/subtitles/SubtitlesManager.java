/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.subtitles;

import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;

import com.noveogroup.vuplayer.FileManager;
import com.noveogroup.vuplayer.VideoPlayer;
import com.noveogroup.vuplayer.utils.PathnameHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import subtitleFile.Caption;
import subtitleFile.TimedTextObject;

public class SubtitlesManager {

    private static final String TAG = "VuPlayer.SubtitlesManager";
    private static final int SUBTITLES_CHECK_DELAY = 100;

    private Handler handler = null;
    private Runnable subtitlingRunnable;
    private Thread subtitlingThread;
    private volatile TimedTextObject currentSubTextObject;
    private ArrayList<TimedTextObject> subTextObjects;
    private int currentSubIndex = 0;

    private VideoPlayer videoPlayer;
    private SubtitlesView subtitlesView;


    public SubtitlesManager(VideoPlayer videoPlayer, SubtitlesView subtitlesView) {
        this.videoPlayer = videoPlayer;
        this.subtitlesView = subtitlesView;
    }

    public void loadSubtitles(String absVideoPathname) {

        ArrayList<String> subPathnames = new ArrayList<String>();
        for (String extension : SubtitlesLoader.EXTENSIONS) {
            String absPathname = PathnameHandler.getWithReplacedExtension(absVideoPathname,
                                                                          extension);
            subPathnames.addAll(FileManager.getFiles(absPathname));
        }
        Collections.sort(subPathnames);
        ArrayList<String> subPathnamesAdditional = new ArrayList<String>();
        for (String extension : SubtitlesLoader.EXTENSIONS) {
            String absPathnamePattern = PathnameHandler.getWithRemovedExtension(absVideoPathname)
                                        + ".*." + extension;
            subPathnamesAdditional.addAll(FileManager.getFiles(absPathnamePattern));
        }
        Collections.sort(subPathnamesAdditional);
        subPathnames.addAll(subPathnamesAdditional);

        subTextObjects = new ArrayList<TimedTextObject>();
        for (String pathname : subPathnames) {
            File subtitlesFile = new File(pathname);

            try {
                FileInputStream stream = new FileInputStream(subtitlesFile);
                TimedTextObject subTextObject = SubtitlesLoader.parseFile(pathname, stream);
                stream.close();
                if(subTextObject != null) {
                    subTextObjects.add(subTextObject);
                }

            } catch (Exception exception) {
                Log.e(TAG, String.format("Can not load subtitles file %s.", pathname));
            }
        }
    }

    public void runSubtitling() {
        handler = new Handler();

        if (currentSubTextObject == null) {
            if(subTextObjects.size() != 0) {
                currentSubTextObject = subTextObjects.get(currentSubIndex);
            } else {
                return;
            }
        }

        subtitlingRunnable = new Runnable() {
            @Override
            public void run() {
                if(videoPlayer.isPlaying()) {
                    int currentPosition = videoPlayer.getCurrentPosition();
                    Collection<Caption> subtitles =  currentSubTextObject.captions.values();
                    for(Caption caption : subtitles) {
                        if(currentPosition >= caption.start.getMilliseconds()
                                && currentPosition <= caption.end.getMilliseconds()) {
                            subtitlesView.setText(Html.fromHtml(caption.content));
//                            subtitlesView.setClickable(true);
//                            subtitlesView.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    subtitlesView.setText("");
//                    subtitlesView.setClickable(false);
//                    subtitlesView.setVisibility(View.INVISIBLE);
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
        if(subtitlingRunnable != null) {
            handler.removeCallbacks(subtitlingRunnable);
            subtitlingRunnable = null;
        }
        if(subtitlingThread != null) {
            subtitlingThread.interrupt();
            subtitlingThread = null;
        }
        subtitlesView.setText("");
    }

    public void changeSubtitling(boolean doSwitchOn) {
        if (doSwitchOn) {
            if(subtitlingThread == null) {
                runSubtitling();
            }
        } else {
            stopSubtitling();
        }
    }
}

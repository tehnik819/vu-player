/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import com.noveogroup.vuplayer.BaseApplication;
import com.noveogroup.vuplayer.events.FilesSearchEvent;
import com.noveogroup.vuplayer.utils.PathnameHandler;

import java.io.File;

public class FilesSearchService extends IntentService {

    public final static String FILES_SEARCH = "VuPlayer.FILES_SEARCH_SERVICE";
    public final static String EXTENSIONS = "VuPlayer.EXTENSIONS";
    public final static String FOUND_FILES = "VuPlayer.FOUND_FILES";
    public final static String IS_FINISHED = "VuPlayer.IS_FINISHED";



    public FilesSearchService() {
        super("FilesSearchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Searching...");

        String[] extensions = intent.getStringArrayExtra(EXTENSIONS);

        processDirectory(Environment.getExternalStorageDirectory().toString(), extensions);

//        Intent onFinishIntent = new Intent(FILES_SEARCH);
//        onFinishIntent.putExtra(IS_FINISHED, true);
//        sendBroadcast(onFinishIntent);

        generateEvent(null, true);

//        BaseApplication.getEventBus().post(new FilesSearchEvent(null, true));

    }

    public void processDirectory(String absDirName, String[] extensions) {

        File directory = new File(absDirName);

        if (!directory.isDirectory()) {
            return;
        }

        String[] filesList = directory.list();
        if (filesList == null) {
            return;
        }

        for (String currentFile : filesList) {
            String currentFileAbsName = PathnameHandler.getAbsPathname(absDirName, currentFile);
            if (new File(currentFileAbsName).isDirectory()) {
                processDirectory(currentFileAbsName, extensions);
            } else {
                for (String currentExtension : extensions) {
                    String pattern = ".*?\\." + currentExtension;
                    if (currentFile.matches(pattern)) {
//                        Intent responseIntent = new Intent(FILES_SEARCH);
//                        responseIntent.putExtra(FOUND_FILES, currentFileAbsName);
//                        responseIntent.putExtra(IS_FINISHED, false);
//                        sendBroadcast(responseIntent);
                        generateEvent(currentFileAbsName, false);
//                        BaseApplication.getEventBus()
//                                .post(new FilesSearchEvent(currentFileAbsName, false));
                        break;
                    }
                }
            }
        }
    }

    private void generateEvent(final String currentFileAbsName, final boolean isFinished) {
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                BaseApplication.getEventBus()
                        .post(new FilesSearchEvent(currentFileAbsName, isFinished));
            }
        });
    }
}

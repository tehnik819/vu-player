/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.noveogroup.vuplayer.subtitles.SubtitlesLoader;
import com.noveogroup.vuplayer.utils.PathnameHandler;

import java.util.ArrayList;
import java.util.Collections;

public class FileManagementTests extends AndroidTestCase {

    private static final String TAG = "FileManagementTests";

    protected String absVideoPathname;

    protected void setUp() {
        absVideoPathname = Environment.getExternalStorageDirectory().toString()
                + getContext().getResources().getString(R.string.filename);
    }

    public void testSubtitlesReading() {

        ArrayList<String> subtitlesPathnames = new ArrayList<String>();
        for (String extension : SubtitlesLoader.EXTENSIONS) {
            String absPathname = PathnameHandler.getWithReplacedExtension(absVideoPathname,
                    extension);
            subtitlesPathnames.addAll(FileManager.getFiles(absPathname));
        }
        Collections.sort(subtitlesPathnames);
        ArrayList<String> subtitlesPathnamesTemp = new ArrayList<String>();
        for (String extension : SubtitlesLoader.EXTENSIONS) {
            String absPathnamePattern = PathnameHandler.getWithRemovedExtension(absVideoPathname)
                    + ".*." + extension;
            subtitlesPathnamesTemp.addAll(FileManager.getFiles(absPathnamePattern));
        }
        Collections.sort(subtitlesPathnamesTemp);
        subtitlesPathnames.addAll(subtitlesPathnamesTemp);

        for (int i = 0; i < subtitlesPathnames.size(); ++i) {
            Log.d(TAG, subtitlesPathnames.get(i));
        }
        assertTrue(subtitlesPathnames.size() != 0);
    }
}

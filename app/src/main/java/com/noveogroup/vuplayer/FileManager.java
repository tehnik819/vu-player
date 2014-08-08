/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import com.noveogroup.vuplayer.utils.PathnameHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileManager {

    public static ArrayList<String> getFiles(String absPathnamePattern) {
        String[] absPathnameSplit = PathnameHandler.getSplitPathname(absPathnamePattern);
        File directory = new File(absPathnameSplit[0]);
        String[] filesList = directory.list();
        ArrayList<String> files = new ArrayList<String>();
        String filenamePattern = absPathnameSplit[1].replace("?", ".?").replace("*", ".*?");
        for (String file : filesList) {
            if (file.matches(filenamePattern)) {
                files.add(PathnameHandler.getAbsPathname(absPathnameSplit[0], file));
            }
        }
        Collections.sort(files);

        return files;
    }


}

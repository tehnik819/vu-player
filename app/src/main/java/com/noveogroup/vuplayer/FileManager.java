/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import com.noveogroup.vuplayer.utils.PathnameHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManager {

    public static List<String> getFiles(String absPathnamePattern) {
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

    public static ArrayList<String> getFiles(ArrayList<String> files, String absDirName, String[] extensions,
                                        boolean doRecursively, boolean areDirs) {

        File directory = new File(absDirName);

        if (!directory.isDirectory()) {
            return files;
        }

        String[] filesList = directory.list();
        if (filesList == null) {
            return files;
        }

        for (String currentFile : filesList) {
            String currentFileAbsName = PathnameHandler.getAbsPathname(absDirName, currentFile);
            if (new File(currentFileAbsName).isDirectory()) {
                if(doRecursively) {
                    files = getFiles(files, currentFileAbsName, extensions, true, areDirs);
                }
            } else {
                for (String currentExtension : extensions) {
                    String pattern = ".*?\\." + currentExtension;
                    if (currentFile.matches(pattern)) {
                        if (areDirs) {
                            if (!files.contains(absDirName)) {
                                files.add(absDirName);
                            }
                        } else {
                            files.add(currentFileAbsName);
                        }
                    }
                }
            }
        }

        Collections.sort(files);

        return files;
    }

    public static ArrayList<String> getFiles(String absDirName, String[] extensions,
                                        boolean doRecursively, boolean areDirs) {

        ArrayList<String> files = new ArrayList<String>();
        files = FileManager.getFiles(files, absDirName, extensions, doRecursively, areDirs);

        return files;
    }

    public static ArrayList<String> getDirsWithFiles(String absDirName, String[] extensions,
                                                boolean doRecursively) {
        return getFiles(absDirName, extensions, doRecursively, true);
    }

    public static ArrayList<String> getNonDirFiles(String absDirName, String[] extensions,
                                              boolean doRecursively) {
        return getFiles(absDirName, extensions, doRecursively, false);
    }
}

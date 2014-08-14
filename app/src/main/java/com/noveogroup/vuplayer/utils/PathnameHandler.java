/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.utils;

import java.util.ArrayList;
import java.util.List;

public class PathnameHandler {

    private PathnameHandler() {
        throw new UnsupportedOperationException("PathnameHandler instance can not be created.");
    }

    public static String[] getSplitPathname(String absPathname) {
        String[] parts = absPathname.split("/");
        String dirName = "";
        String filename;

        for (int i = 0; i < parts.length - 1; ++i) {
            dirName += parts[i] + "/";
        }
        filename = parts[parts.length - 1];

        return new String[] {dirName, filename};
    }

    public static String getAbsPathname(final String dirName, final String filename) {
        char lastSymbol = dirName.toCharArray()[dirName.length() - 1];
        return lastSymbol == '/' ? dirName + filename : dirName + "/" + filename;
    }

    public static String getWithRemovedExtension(final String pathname) {
        if(pathname == null) {
            return null;
        }
        String[] parts = pathname.split("\\.");
        String finalString = parts[0];
        for (int i = 1; i < parts.length - 1; ++i) {
            finalString += "." + parts[i];
        }

        return finalString;
    }

    public static String getWithReplacedExtension(final String pathname, final String extension) {
        if (pathname == null || extension == null) {
            return null;
        }

        return getWithRemovedExtension(pathname) + "." + extension;
    }

    public static List<String> getRemovedFiles(ArrayList<String> oldPathnames,
                                               ArrayList<String> newPathnames) {

        ArrayList<String> removedFiles = new ArrayList<String>();
        for (String currentPathname : oldPathnames) {
            if (!newPathnames.contains(currentPathname)) {
                removedFiles.add(currentPathname);
            }
        }

        return removedFiles;
    }
}

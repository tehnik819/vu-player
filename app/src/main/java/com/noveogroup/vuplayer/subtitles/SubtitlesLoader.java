/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.subtitles;

import java.io.IOException;
import java.io.InputStream;

import subtitleFile.FatalParsingException;
import subtitleFile.FormatASS;
import subtitleFile.FormatSCC;
import subtitleFile.FormatSRT;
import subtitleFile.FormatSTL;
import subtitleFile.FormatTTML;
import subtitleFile.TimedTextObject;

public class SubtitlesLoader {

    public static final String[] EXTENSIONS = {"ass", "scc", "srt", "ssa", "stl", "xml"};

    public static TimedTextObject parseFile(String filename, InputStream stream)
            throws IOException, FatalParsingException {

        String[] filenameSplit = filename.split("\\.");
        System.out.println(filenameSplit.length);
        for(int i = 0; i < filenameSplit.length; ++i) {
            System.out.println(filenameSplit[i]);
        }
        String extension = filenameSplit[filenameSplit.length - 1];

        if (extension.equalsIgnoreCase("ass") || extension.equalsIgnoreCase("ssa")) {
            return new FormatASS().parseFile(filename, stream);
        }
        if (extension.equalsIgnoreCase("scc")) {
             return new FormatSCC().parseFile(filename, stream);
        }
        if (extension.equalsIgnoreCase("srt")) {
            return new FormatSRT().parseFile(filename, stream);
        }
        if (extension.equalsIgnoreCase("stl")) {
            return new FormatSTL().parseFile(filename, stream);
        }
        if (extension.equalsIgnoreCase("xml")) {
            return new FormatTTML().parseFile(filename, stream);
        }
        return null;
    }
}

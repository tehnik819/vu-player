/*******************************************************************************
 * Copyright © 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.utils;

public final class TimeConverter {

    private TimeConverter() {
        throw new UnsupportedOperationException("TimeConverter instance can not be created.");
    }

    public static String convertToString(int currentTime, int duration) {
        int hours = currentTime / 60 / 60 / 1000;
        int minutes = currentTime / 60 / 1000 - hours * 60;
        int seconds = currentTime / 1000 - minutes * 60 - hours * 60 * 60;

        if (duration / 60 / 60 / 1000 >= 1) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}

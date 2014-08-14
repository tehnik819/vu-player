/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.utils;

import android.content.res.Resources;

import com.noveogroup.vuplayer.translation.Languages;

public class ResourcesHandler {

    private ResourcesHandler() {
        throw new UnsupportedOperationException("ResourcesHandler instance can not be created.");
    }

    public static Languages getLanguages(Resources resources, int arrayId, String splitWith) {
        String[] itemsString = resources.getStringArray(arrayId);
        String[] languagesNamesFull = new String[itemsString.length];
        String[] languagesNamesShort = new String[itemsString.length];

        for (int i = 0; i < itemsString.length; ++i) {
            String[] itemSplit = itemsString[i].split(splitWith);
            languagesNamesFull[i] = itemSplit[0];
            languagesNamesShort[i] = itemSplit[1];
        }

        return new Languages(languagesNamesFull, languagesNamesShort);
    }
}

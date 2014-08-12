/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation;

import android.os.Parcelable;

public interface Translator extends Parcelable{

    String getText();
    String getSourceLanguage();
    String getTranslationLanguage();
    String getPrimaryTranslation();
    String getDetailedTranslation();
    void retrieveTranslation();
    void setText(String text);
    void setSourceLanguage(String sourceLanguage);
    void setTranslationLanguage(String translationLanguage);
}

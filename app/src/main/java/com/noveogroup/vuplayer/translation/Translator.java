/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation;

import android.content.Context;
import android.os.Parcelable;
import android.text.SpannableString;

public interface Translator extends Parcelable{

    String getText();
    String getSourceLanguage();
    String getTranslationLanguage();
    boolean isFinished();

    void setText(String text);
    void setSourceLanguage(String sourceLanguage);
    void setTranslationLanguage(String translationLanguage);
    void setFinished(boolean state);

    String getPrimaryTranslation();
    SpannableString getDetailedTranslation(Context context);
    void retrieveTranslation(Context context);
}

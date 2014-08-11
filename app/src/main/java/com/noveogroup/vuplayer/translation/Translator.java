/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation;

import android.os.Parcelable;

public interface Translator extends Parcelable{

    String getPrimaryTranslation();
    String getDetailedTranslation();
    void retrieveTranslation();
}

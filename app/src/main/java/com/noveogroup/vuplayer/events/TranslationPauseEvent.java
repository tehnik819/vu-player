/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.events;

public class TranslationPauseEvent {
    public String currentSourceLanguage;
    public String currentTranslationLanguage;

    public TranslationPauseEvent(String currentSourceLanguage, String currentTranslationLanguage) {
        this.currentSourceLanguage = currentSourceLanguage;
        this.currentTranslationLanguage = currentTranslationLanguage;
    }
}

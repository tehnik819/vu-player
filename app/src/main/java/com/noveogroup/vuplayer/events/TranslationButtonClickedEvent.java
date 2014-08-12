/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.events;

public class TranslationButtonClickedEvent {

    public String textToTranslate;

    public TranslationButtonClickedEvent(String textToTranslate) {
        this.textToTranslate = textToTranslate;
    }
}

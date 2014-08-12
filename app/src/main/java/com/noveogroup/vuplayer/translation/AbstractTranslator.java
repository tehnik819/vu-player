/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation;

public abstract class AbstractTranslator implements Translator {

    protected String text;
    protected String sourceLanguage;
    protected String translationLanguage;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    @Override
    public String getTranslationLanguage() {
        return translationLanguage;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    @Override
    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }
}

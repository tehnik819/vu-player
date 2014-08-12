/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation;

public abstract class AbstractTranslator implements Translator {

    protected String text;
    protected String sourceLanguage;
    protected String translationLanguage;
    protected boolean isFinished = false;

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
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            isFinished = false;
        }
    }

    @Override
    public void setSourceLanguage(String sourceLanguage) {
        if (!this.sourceLanguage.equals(sourceLanguage)) {
            this.sourceLanguage = sourceLanguage;
            isFinished = false;
        }
    }

    @Override
    public void setTranslationLanguage(String translationLanguage) {
        if (!this.translationLanguage.equals(translationLanguage)) {
            this.translationLanguage = translationLanguage;
            isFinished = false;
        }
    }

    @Override
    public void setFinished(boolean state) {
        isFinished = state;
    }
}

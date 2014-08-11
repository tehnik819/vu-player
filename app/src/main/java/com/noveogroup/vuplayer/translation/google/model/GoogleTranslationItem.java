/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation.google.model;

import java.util.List;

public class GoogleTranslationItem {
    private List<GoogleSentence> sentences;
    private List<GooglePartOfSpeech> dict;
    private String src;
    private GoogleSpell spell;
    private Integer serverTime;

    public List<GoogleSentence> getSentences() {
        return sentences;
    }

    public String getSrc() {
        return src;
    }
}



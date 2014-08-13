/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.events;

import com.noveogroup.vuplayer.translation.Translator;

public class MoreButtonClickEvent {
    public Translator translator;

    public MoreButtonClickEvent(Translator translator) {
        this.translator = translator;
    }
}

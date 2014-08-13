/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.events;

public class AddButtonClickEvent {
    public String text;
    public String comment;

    public AddButtonClickEvent(String text, String comment) {
        this.text = text;
        this.comment = comment;
    }
}

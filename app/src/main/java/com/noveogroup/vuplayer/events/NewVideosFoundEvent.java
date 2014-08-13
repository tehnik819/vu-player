/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.events;

import java.util.ArrayList;

public class NewVideosFoundEvent {
    public ArrayList<String> videos;

    public NewVideosFoundEvent(ArrayList<String> videos) {
        this.videos = videos;
    }
}

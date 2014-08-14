/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.events;

import java.util.ArrayList;
import java.util.List;

public class VideosRemovedEvent {
    public List<String> removedVideos;

    public VideosRemovedEvent(List<String> removedVideos) {
        this.removedVideos = removedVideos;
    }
}

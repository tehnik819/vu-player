/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.events;

public class FilesSearchEvent {
    public String filePath;
    public boolean isFinished;

    public FilesSearchEvent(String filePath, boolean isFinished) {
        this.filePath = filePath;
        this.isFinished = isFinished;
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BaseApplication extends Application {

    private static Bus eventBus;

    public static Bus getEventBus() {
        return eventBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        eventBus = new Bus(ThreadEnforcer.ANY);
    }
}

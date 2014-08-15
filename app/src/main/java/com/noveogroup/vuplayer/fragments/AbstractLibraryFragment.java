/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.noveogroup.vuplayer.BaseApplication;
import com.noveogroup.vuplayer.LibraryAdapter;
import com.noveogroup.vuplayer.events.LibraryItemClickEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLibraryFragment extends ListFragment {

    protected static final String ITEMS = "VuPlayer.ITEMS";
    protected static final String ITEM_ICON_ID = "VuPlayer.ITEM_ICON_ID";

    protected ArrayList<String> items;
    protected int itemIconId;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(ITEMS, items);
        outState.putInt(ITEM_ICON_ID, itemIconId);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        BaseApplication.getEventBus()
                .post(new LibraryItemClickEvent(((LibraryAdapter) listView.getAdapter())
                        .getItem(position)));
    }

    public ArrayList<String> getItemsList() {
        return items;
    }
}

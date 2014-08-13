/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.noveogroup.vuplayer.BaseApplication;
import com.noveogroup.vuplayer.LibraryAdapter;
import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.events.NewVideosFoundEvent;
import com.noveogroup.vuplayer.events.RescanActionClickEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class LibraryVideoFragment extends LibraryFragment {

    public static LibraryVideoFragment newInstance(ArrayList<String> items, int itemIconId) {
        LibraryVideoFragment fragment = new LibraryVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ITEMS, items);
        bundle.putInt(ITEM_ICON_ID, itemIconId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        BaseApplication.getEventBus().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BaseApplication.getEventBus().unregister(this);
    }

    @Subscribe
    public void onNewVideosFound(NewVideosFoundEvent event) {
        for (String currentItem : event.videos) {
            if (!items.contains(currentItem)) {
                ((LibraryAdapter) getListAdapter()).add(currentItem);
            }
        }
        ((LibraryAdapter) getListAdapter()).notifyDataSetChanged();
        items = event.videos;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_library_video, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_rescan:
                BaseApplication.getEventBus().post(new RescanActionClickEvent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

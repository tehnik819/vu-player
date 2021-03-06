/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.noveogroup.vuplayer.events.FilesSearchEvent;
import com.noveogroup.vuplayer.events.NewVideosFoundEvent;
import com.noveogroup.vuplayer.events.RescanActionClickEvent;
import com.noveogroup.vuplayer.services.FilesSearchService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public final class LibraryVideoFragment extends AbstractLibraryFragment {

    private final static String IS_SCANNING = "VuPlayer.IS_SCANNING";

    private boolean isScanning = false;

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

        if (savedInstanceState != null) {
            items = savedInstanceState.getStringArrayList(ITEMS);
            itemIconId = savedInstanceState.getInt(ITEM_ICON_ID);
            if (items != null) {
                isScanning = savedInstanceState.getBoolean(IS_SCANNING);
            }
        }

        if (items == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                items = bundle.getStringArrayList(ITEMS);
                itemIconId = bundle.getInt(ITEM_ICON_ID);
            }
        }

        if (items == null) {
            items = new ArrayList<String>();
            runFilesSearch();
        }
        LibraryAdapter adapter = new LibraryAdapter(getActivity(), items, itemIconId);
        setListAdapter(adapter);

//        Notify that there are some items to add to action bar.
        setHasOptionsMenu(true);

//        Register with event bus.
        BaseApplication.getEventBus().register(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_SCANNING, isScanning);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        Unregister with event bus.
        BaseApplication.getEventBus().unregister(this);
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
                if (!isScanning) {
                    ((LibraryAdapter) getListAdapter()).clear();
                    ((LibraryAdapter) getListAdapter()).notifyDataSetChanged();
                    runFilesSearch();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Subscribe
    public void onFilesSearch(FilesSearchEvent event) {
        isScanning = !event.isFinished;
        if (isScanning) {
            String file = event.filePath;
            if (!items.contains(file)) {
                items.add(file);
                System.out.println(file);
            }
            ((LibraryAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    private void runFilesSearch() {
        String[] extensions = getResources().getStringArray(R.array.supported_video_formats);
        Intent intent = new Intent(getActivity(), FilesSearchService.class);
        intent.putExtra(FilesSearchService.EXTENSIONS, extensions);
        isScanning = true;
        getActivity().startService(intent);
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.noveogroup.vuplayer.BaseApplication;
import com.noveogroup.vuplayer.LibraryAdapter;
import com.noveogroup.vuplayer.events.LibraryItemClickEvent;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends AbstractLibraryFragment {

    public static LibraryFragment newInstance(ArrayList<String> items, int itemIconId) {
        LibraryFragment fragment = new LibraryFragment();
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
        }
        LibraryAdapter adapter = new LibraryAdapter(getActivity(), items, itemIconId);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

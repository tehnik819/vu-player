/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LibraryAdapter extends ArrayAdapter<String> {

    private Bitmap itemBitmap;

    public LibraryAdapter(Context context, List<String> items, int itemIconId) {
        super(context, 0, items);
        itemBitmap = BitmapFactory.decodeResource(getContext().getResources(), itemIconId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView itemNameView = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.library_item, parent, false);

            ((ImageView) convertView.findViewById(R.id.library_item_icon_view))
                    .setImageBitmap(itemBitmap);
            itemNameView = (TextView) convertView.findViewById(R.id.library_item_name_view);

            convertView.setTag(itemNameView);
        }

        itemNameView = itemNameView == null ? (TextView) convertView.getTag() : itemNameView;
        itemNameView.setText(getItem(position));

        return convertView;
    }
}

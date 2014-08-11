/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation.google;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.translation.Translator;
import com.noveogroup.vuplayer.translation.google.model.GoogleTranslationItem;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public final class GoogleTranslator implements Translator {

    private static final String TAG = "VuPlayer.GoogleTranslator";

    private String request;
    private GoogleTranslationItem translationItem;
    private String primaryTranslation;
    private String detailedTranslation;

    public GoogleTranslator(String request) {
        this.request = request;
    }

    private GoogleTranslator(Parcel parcel) {
        request = parcel.readString();
    }

    @Override
    public String getPrimaryTranslation() {
        if (primaryTranslation != null) {
            return primaryTranslation;
        }

        primaryTranslation = translationItem != null
                ? translationItem.getSentences().get(0).getTrans() : null;

        return primaryTranslation;
    }

    @Override
    public String getDetailedTranslation() {
        return null;
    }

    @Override
    public void retrieveTranslation() {
        try {
            URL url = new URL(request);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String jsonString = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            urlConnection.disconnect();

            if (jsonString != null) {
                Gson gson = new Gson();
                translationItem = gson.fromJson(jsonString, GoogleTranslationItem.class);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Can not retrieve translation.");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(request);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new GoogleTranslator(parcel);
        }

        @Override
        public Object[] newArray(int size) {
            return new GoogleTranslator[size];
        }
    };
}

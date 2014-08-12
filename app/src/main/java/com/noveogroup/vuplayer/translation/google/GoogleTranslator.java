/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation.google;

import android.os.Parcel;
import android.util.Log;

import com.google.gson.Gson;
import com.noveogroup.vuplayer.translation.AbstractTranslator;
import com.noveogroup.vuplayer.translation.Translator;
import com.noveogroup.vuplayer.translation.google.model.GoogleTranslationItem;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public final class GoogleTranslator extends AbstractTranslator {

    private static final String TAG = "VuPlayer.GoogleTranslator";

    private String requestString;
    private GoogleTranslationItem translationItem;
    private String primaryTranslation;
    private String detailedTranslation;

    public GoogleTranslator(String text, String sourceLanguage, String translationLanguage, String requestString) {
        this.text = text;
        this.sourceLanguage = sourceLanguage;
        this.translationLanguage = translationLanguage;
        this.requestString = requestString;
    }

    private GoogleTranslator(Parcel parcel) {
        text = parcel.readString();
        sourceLanguage = parcel.readString();
        translationLanguage = parcel.readString();
        requestString = parcel.readString();
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
            final OkHttpClient client = new OkHttpClient();
            final String requestFilled = String.format(requestString, text,
                                                       sourceLanguage, translationLanguage);
            final Request request = new Request.Builder().url(requestFilled).build();
            Response response = client.newCall(request).execute();

            String jsonString = response.body().string();
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
        parcel.writeString(text);
        parcel.writeString(sourceLanguage);
        parcel.writeString(translationLanguage);
        parcel.writeString(requestString);
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

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation.google;

import android.content.Context;
import android.os.Parcel;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.google.gson.Gson;
import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.translation.AbstractTranslator;
import com.noveogroup.vuplayer.translation.google.model.GooglePartOfSpeech;
import com.noveogroup.vuplayer.translation.google.model.GoogleTranslationItem;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class GoogleTranslator extends AbstractTranslator {

    private static final String TAG = "VuPlayer.GoogleTranslator";

    private String translationJson;

    public GoogleTranslator(String text, String sourceLanguage, String translationLanguage) {
        this.text = text;
        this.sourceLanguage = sourceLanguage;
        this.translationLanguage = translationLanguage;
    }

    private GoogleTranslator(Parcel parcel) {
        text = parcel.readString();
        sourceLanguage = parcel.readString();
        translationLanguage = parcel.readString();
        boolean[] array = new boolean[1];
        parcel.readBooleanArray(array);
        isFinished = array[0];
        translationJson = parcel.readString();
    }

    @Override
    public String getPrimaryTranslation() {
        if(translationJson == null) {
            return null;
        }

        Gson gson = new Gson();
        GoogleTranslationItem translationItem = gson.fromJson(translationJson,
                GoogleTranslationItem.class);

        return translationItem != null ? translationItem.getSentences().get(0).getTrans() : null;
    }

    @Override
    public SpannableString getDetailedTranslation(Context context) {
        if(translationJson == null) {
            return null;
        }

        Gson gson = new Gson();
        GoogleTranslationItem translationItem = gson.fromJson(translationJson,
                GoogleTranslationItem.class);

        String translationString = "";
        List<GooglePartOfSpeech> dict = translationItem.getDict();

        if (dict == null) {
            return new SpannableString(getPrimaryTranslation());
        }

        List<int[]> highlightIntervals = new ArrayList<int[]>();
        for (GooglePartOfSpeech partOfSpeech : dict) {
            String pos = partOfSpeech.getPos();
            highlightIntervals.add(new int[] {translationString.length(),
                    translationString.length() + pos.length()});
            translationString += pos + "\n";
            List<String> terms = partOfSpeech.getTerms();
            for (String term : terms) {
                translationString += "- " + term + "\n";
            }
        }

        SpannableString detailedTranslation = new SpannableString(translationString);
        for(int[] interval : highlightIntervals) {
            ForegroundColorSpan partOfSpeechSpan = new ForegroundColorSpan(context.getResources()
                    .getColor(R.color.translation_part_of_speech_text));
            detailedTranslation.setSpan(partOfSpeechSpan, interval[0], interval[1],
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return detailedTranslation;
    }

    @Override
    public void retrieveTranslation(Context context) {

        isFinished = false;
        String requestString = context.getResources()
                .getString(R.string.translate_google_http_request);
        try {
            final OkHttpClient client = new OkHttpClient();
            final String encodedText = URLEncoder.encode(text, "UTF-8");
            final String requestFilled = String.format(requestString, encodedText,
                                                       sourceLanguage, translationLanguage);
            final Request request = new Request.Builder().url(requestFilled).build();
            Response response = client.newCall(request).execute();

            translationJson = response.body().string();
        } catch (Exception exception) {
            Log.e(TAG, "Can not retrieve translation.");
        }
        isFinished = true;
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
        parcel.writeBooleanArray(new boolean[]{isFinished});
        parcel.writeString(translationJson);
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

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.noveogroup.vuplayer.translation.google.model.GoogleTranslationItem;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class TranslationTests extends AndroidTestCase {

    private static final String TAG = "TranslationTests";

    private static final String text = "hello";
    private static final String sourceLanguage = "en";
    private static final String translationLanguage = "ru";

    public void testTranslateGoogle() {
        final String request = getContext().getResources()
                .getString(R.string.translate_google_http_request, text,
                        sourceLanguage, translationLanguage);
        System.out.println(request);

        try {
            URL url = new URL(request);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String jsonString = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            urlConnection.disconnect();
            Log.d(TAG, jsonString);
            if (jsonString != null) {
                Gson gson = new Gson();
                GoogleTranslationItem item = gson.fromJson(jsonString, GoogleTranslationItem.class);
                if (item != null) {
                    Log.d(TAG, item.getSrc());
                    Log.d(TAG, item.getSentences().get(0).getTrans());
                }
                assertTrue(item != null);
                return;
            }
            assertTrue(false);
        } catch (JsonSyntaxException exception) {
            Log.e(TAG, "Can not retrieve translation: " + exception.getMessage());
            assertTrue(false);

        } catch (Exception exception) {
            Log.e(TAG, "Can not retrieve translation");
            assertTrue(false);
        }

    }
}

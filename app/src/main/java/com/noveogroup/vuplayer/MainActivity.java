/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.noveogroup.vuplayer.events.TranslationButtonClickedEvent;
import com.noveogroup.vuplayer.fragments.Library;
import com.noveogroup.vuplayer.fragments.PrimaryTranslationFragment;
import com.noveogroup.vuplayer.translation.google.GoogleTranslator;
import com.squareup.otto.Subscribe;

import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    public final static String DEBUG_TAG = "VuPlayer.DEBUG_MAIN_ACTIVITY";
    public final static String SHARED_PREFERENCES_NAME = "com.noveogroup.vuplayer";
    public final static String PREFS_SOURCE_LANGUAGE = "SOURCE_LANGUAGE";
    public final static String PREFS_TRANSLATION_LANGUAGE = "TRANSLATION_LANGUAGE";
    public final static String TRANSLATION_DIALOG = "VuPlayer.TRANSLATION_DIALOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new Library())
                    .commit();
        }
        
//        Set hardware volume buttons to work in the Activity.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

//        Register with the event bus.
        BaseApplication.getEventBus().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Unregister with the event bus.
        BaseApplication.getEventBus().unregister(this);
    }

    @Subscribe
    public void onTranslationButtonClick(TranslationButtonClickedEvent event) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String sourceLanguage = preferences.getString(PREFS_SOURCE_LANGUAGE,
                getResources().getString(R.string.source_language_default));
        String translationLanguage = preferences.getString(PREFS_TRANSLATION_LANGUAGE,
                getResources().getString(R.string.translation_language_default));
        String text = event.textToTranslate.replace(' ', '+');
        GoogleTranslator translator = new GoogleTranslator(text, sourceLanguage,
                                                           translationLanguage);
        PrimaryTranslationFragment fragment = PrimaryTranslationFragment.newInstance(translator);
        fragment.show(getSupportFragmentManager(), TRANSLATION_DIALOG);
    }
}

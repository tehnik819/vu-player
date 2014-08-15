/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.noveogroup.vuplayer.events.AddButtonClickEvent;
import com.noveogroup.vuplayer.events.FilesSearchEvent;
import com.noveogroup.vuplayer.events.LibraryItemClickEvent;
import com.noveogroup.vuplayer.events.MoreButtonClickEvent;
import com.noveogroup.vuplayer.events.NewVideosFoundEvent;
import com.noveogroup.vuplayer.events.RescanActionClickEvent;
import com.noveogroup.vuplayer.events.TranslateButtonClickEvent;
import com.noveogroup.vuplayer.events.TranslationPauseEvent;
import com.noveogroup.vuplayer.events.VideosRemovedEvent;
import com.noveogroup.vuplayer.fragments.AbstractLibraryFragment;
import com.noveogroup.vuplayer.fragments.DetailedTranslationFragment;
import com.noveogroup.vuplayer.fragments.LibraryFragment;
import com.noveogroup.vuplayer.fragments.LibraryVideoFragment;
import com.noveogroup.vuplayer.fragments.NotesFragment;
import com.noveogroup.vuplayer.fragments.NotesList;
import com.noveogroup.vuplayer.fragments.PrimaryTranslationFragment;
import com.noveogroup.vuplayer.fragments.VideoFragment;
import com.noveogroup.vuplayer.services.FilesSearchService;
import com.noveogroup.vuplayer.translation.google.GoogleTranslator;
import com.noveogroup.vuplayer.utils.FragmentTransactionHandler;
import com.noveogroup.vuplayer.utils.PathnameHandler;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    public static enum FragmentType {
        ROOT_PAGE,
        VIDEOS_PAGE,
        NOTES_PAGE,
        VIDEO_SCREEN,
        TRANSLATION_DIALOG,
        DETAILED_TRANSLATION_PAGE}

    public final static String TAG = "VuPlayer.MainActivity";

    private final static String VIDEOS_PATHS = "VuPlayer.VIDEOS_PATHS";
    private final static String CURRENT_VIDEO_NAME = "VuPlayer.CURRENT_VIDEO_NAME";

    public final static String SHARED_PREFERENCES_NAME = "com.noveogroup.vuplayer";
    public final static String PREFS_SOURCE_LANGUAGE = "SOURCE_LANGUAGE";
    public final static String PREFS_TRANSLATION_LANGUAGE = "TRANSLATION_LANGUAGE";

    private ArrayList<String> videosPaths;
    private String currentVideoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            ArrayList<String> items = new ArrayList<String>();
            items.add(getResources().getString(R.string.video_folder_name));
            items.add(getResources().getString(R.string.notes_folder_name));

            LibraryFragment fragment = LibraryFragment.newInstance(items, R.drawable.ic_folder);
            FragmentTransactionHandler.putFragment(getSupportFragmentManager(),
                    R.id.container, fragment, FragmentType.ROOT_PAGE.toString(), false);
        } else {
            videosPaths = savedInstanceState.getStringArrayList(VIDEOS_PATHS);
            currentVideoName = savedInstanceState.getString(CURRENT_VIDEO_NAME);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Unregister with the event bus.
        BaseApplication.getEventBus().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(VIDEOS_PATHS, videosPaths);
        outState.putString(CURRENT_VIDEO_NAME, currentVideoName);
    }

    @Subscribe
    public void onTranslateButtonClick(TranslateButtonClickEvent event) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String sourceLanguage = preferences.getString(PREFS_SOURCE_LANGUAGE,
                getResources().getString(R.string.source_language_default));
        String translationLanguage = preferences.getString(PREFS_TRANSLATION_LANGUAGE,
                getResources().getString(R.string.translation_language_default));
        GoogleTranslator translator = new GoogleTranslator(event.textToTranslate, sourceLanguage,
                                                           translationLanguage);
        PrimaryTranslationFragment fragment = PrimaryTranslationFragment.newInstance(translator);
        fragment.show(getSupportFragmentManager(), FragmentType.TRANSLATION_DIALOG.toString());
    }

    @Subscribe
    public void onAddButtonClick(AddButtonClickEvent event) {
        NotesFragment fragment = NotesFragment.newInstance(event.text,
                currentVideoName, event.comment);
        FragmentTransactionHandler.putFragment(getSupportFragmentManager(), R.id.container,
                fragment, FragmentType.NOTES_PAGE.toString(), true);
    }

    @Subscribe
    public void onMoreButtonClick(MoreButtonClickEvent event) {
        DetailedTranslationFragment fragment =
                DetailedTranslationFragment.newInstance(event.translator);
        FragmentTransactionHandler.putFragment(getSupportFragmentManager(), R.id.container,
                fragment, FragmentType.DETAILED_TRANSLATION_PAGE.toString(), true);
    }

    @Subscribe
    public void onLibraryItemClick(LibraryItemClickEvent event) {

        try {
            FragmentType tag = Enum.valueOf(FragmentType.class,
                    getSupportFragmentManager().findFragmentById(R.id.container).getTag());

            switch (tag) {
                case ROOT_PAGE:
                    if (event.itemName
                            .equals(getResources().getString(R.string.video_folder_name))) {
                        openVideoPage();
                    } else if (event.itemName
                            .equals(getResources().getString(R.string.notes_folder_name))) {
                        openNotesPage();
                    }
                    break;
                case VIDEOS_PAGE:
                    currentVideoName = event.itemName;
                    videosPaths = ((AbstractLibraryFragment) getSupportFragmentManager()
                            .findFragmentByTag(FragmentType.VIDEOS_PAGE.toString())).getItemsList();
                    if (new File(currentVideoName).canRead()) {
                        VideoFragment fragment = VideoFragment.newInstance(currentVideoName);
                        FragmentTransactionHandler.putFragment(getSupportFragmentManager(),
                                R.id.container, fragment, FragmentType.VIDEO_SCREEN.toString(),
                                true);
                    }
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception: ", exception);
        }
    }

    private void openVideoPage() {
        LibraryVideoFragment fragment = LibraryVideoFragment.newInstance(videosPaths,
                R.drawable.ic_video);
        FragmentTransactionHandler.putFragment(getSupportFragmentManager(), R.id.container,
                fragment, FragmentType.VIDEOS_PAGE.toString(), true);
    }

    private void openNotesPage() {
        FragmentTransactionHandler.putFragment(getSupportFragmentManager(), R.id.container,
                new NotesList(), FragmentType.NOTES_PAGE.toString(), true);
    }

    @Subscribe
    public void onTranslationPause(TranslationPauseEvent event) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFS_SOURCE_LANGUAGE, event.currentSourceLanguage);
        editor.putString(PREFS_TRANSLATION_LANGUAGE, event.currentTranslationLanguage);
        editor.apply();
    }
}

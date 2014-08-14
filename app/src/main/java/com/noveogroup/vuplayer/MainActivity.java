/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.noveogroup.vuplayer.events.AddButtonClickEvent;
import com.noveogroup.vuplayer.events.FilesSearchEvent;
import com.noveogroup.vuplayer.events.LibraryItemClickEvent;
import com.noveogroup.vuplayer.events.MoreButtonClickEvent;
import com.noveogroup.vuplayer.events.NewVideosFoundEvent;
import com.noveogroup.vuplayer.events.RescanActionClickEvent;
import com.noveogroup.vuplayer.events.TranslateButtonClickEvent;
import com.noveogroup.vuplayer.events.TranslationPauseEvent;
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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    public static enum FragmentType {
        ROOT_PAGE,
        VIDEOS_PAGE,
        NOTES_PAGE,
        VIDEO_SCREEN,
        TRANSLATION_DIALOG,
        DETAILED_TRANSLATION_PAGE}

    public final static String TAG = "VuPlayer.MainActivity";

    public final static String TRANSLATION_DIALOG = "VuPlayer.TRANSLATION_DIALOG";
//    public final static String ROOT_PAGE = "VuPlayer.ROOT_PAGE";

    public final static String SHARED_PREFERENCES_NAME = "com.noveogroup.vuplayer";
    public final static String PREFS_SOURCE_LANGUAGE = "SOURCE_LANGUAGE";
    public final static String PREFS_TRANSLATION_LANGUAGE = "TRANSLATION_LANGUAGE";
    public final static String PREFS_VIDEO_FOLDERS = "VIDEO_FOLDERS";

    private ArrayList<String> videosPaths = new ArrayList<String>();
    private ArrayList<String> videosPathsTrimmed = new ArrayList<String>();
    private VideoFragment fragment;
    private boolean isScanning = false;
    private String currentVideoName;

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(FilesSearchService.FILES_SEARCH)) {
//                isScanning = !intent.getBooleanExtra(FilesSearchService.IS_FINISHED, true);
//                if (isScanning) {
//                    String file = intent.getStringExtra(FilesSearchService.FOUND_FILES);
//                    if (!videosPaths.contains(file)) {
//                        videosPaths.add(file);
//                    }
//                    BaseApplication.getEventBus().post(new NewVideosFoundEvent(videosPaths));
//                }
//
//            }
//        }
//    };

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

            runFilesSearch();
        }

//        Set hardware volume buttons to work in the Activity.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

//        Register with the event bus.
        BaseApplication.getEventBus().register(this);

//        Register BroadcastReceiver to receive Intents from FilesSearchService.
//        registerReceiver(broadcastReceiver, new IntentFilter(FilesSearchService.FILES_SEARCH));
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

//        Unregister BroadcastReceiver.
//        unregisterReceiver(broadcastReceiver);
    }

    @Subscribe
    public void onFilesSearch(FilesSearchEvent event) {
        isScanning = !event.isFinished;
        if (isScanning) {
            String file = event.filePath;
            if (!videosPaths.contains(file)) {
                videosPaths.add(file);
            }
            BaseApplication.getEventBus().post(new NewVideosFoundEvent(videosPaths));
        }
    }

    @Subscribe
    public void onTranslateButtonClick(TranslateButtonClickEvent event) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String sourceLanguage = preferences.getString(PREFS_SOURCE_LANGUAGE,
                getResources().getString(R.string.source_language_default));
        String translationLanguage = preferences.getString(PREFS_TRANSLATION_LANGUAGE,
                getResources().getString(R.string.translation_language_default));
        String text = event.textToTranslate.replace(' ', '+');
        GoogleTranslator translator = new GoogleTranslator(text, sourceLanguage,
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
                    fragment = VideoFragment.newInstance(currentVideoName);
                    FragmentTransactionHandler.putFragment(getSupportFragmentManager(),
                            R.id.container, fragment, FragmentType.VIDEO_SCREEN.toString(), true);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception: ", exception);
        }
    }

    private void openVideoPage() {

        LibraryVideoFragment fragment = LibraryVideoFragment.newInstance(videosPaths,
                R.drawable.ic_launcher);
        FragmentTransactionHandler.putFragment(getSupportFragmentManager(), R.id.container,
                fragment, FragmentType.VIDEOS_PAGE.toString(), true);
    }

    private void openNotesPage() {
        FragmentTransactionHandler.putFragment(getSupportFragmentManager(), R.id.container,
                new NotesList(), FragmentType.NOTES_PAGE.toString(), true);
    }

    @Subscribe
    public void onRescanActionClick(RescanActionClickEvent event) {
        if (!isScanning) {
            runFilesSearch();
        }
    }

    private void runFilesSearch() {
        String[] extensions = getResources().getStringArray(R.array.supported_video_formats);
        Intent intent = new Intent(this, FilesSearchService.class);
        intent.putExtra(FilesSearchService.EXTENSIONS, extensions);
        startService(intent);
        isScanning = true;
    }

    @Subscribe
    public void onTranslationPause(TranslationPauseEvent event) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFS_SOURCE_LANGUAGE, event.currentSourceLanguage);
        editor.putString(PREFS_TRANSLATION_LANGUAGE, event.currentTranslationLanguage);
        editor.commit();
    }
}

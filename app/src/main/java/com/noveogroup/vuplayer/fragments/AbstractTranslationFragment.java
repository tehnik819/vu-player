/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.noveogroup.vuplayer.translation.Translator;

public abstract class AbstractTranslationFragment extends DialogFragment {

    protected static final String TRANSLATOR = "VuPlayer.TranslationFragment.TRANSLATOR";
    protected static final String IS_FINISHED = "VuPlayer.TranslationFragment.IS_FINISHED";

    protected Translator translator;
    protected boolean isFinished = false;

    protected AsyncTask<Void, Void, Void> translationTask;

    protected static AbstractTranslationFragment initialize(AbstractTranslationFragment fragment,
                                                            Translator translator) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TRANSLATOR, translator);
        fragment.setArguments(bundle);

        return fragment;
    }

    protected void retrieveArguments(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            translator = savedInstanceState.getParcelable(TRANSLATOR);
            isFinished = savedInstanceState.getBoolean(IS_FINISHED);
        } else {
            Bundle bundle = getArguments();
            translator = bundle != null ? (Translator) bundle.getParcelable(TRANSLATOR) : null;
        }
    }

    protected void retrieveTranslation() {
        isFinished = false;
        if (translator != null) {
            translationTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    translator.retrieveTranslation();
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (!isCancelled()) {
                        super.onPostExecute(result);
                        showTranslation();
                    }
                    isFinished = true;
                }
            }.execute();
        }
    }

    protected abstract void showTranslation();

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (translationTask != null) {
            translationTask.cancel(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRANSLATOR, translator);
        outState.putBoolean(IS_FINISHED, isFinished);
    }
}

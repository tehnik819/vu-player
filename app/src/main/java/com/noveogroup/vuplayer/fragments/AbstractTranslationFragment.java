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

    protected Translator translator;

    protected AsyncTask<Void, Void, Void> translationTask;

    protected static AbstractTranslationFragment initialize(AbstractTranslationFragment fragment,
                                                            Translator translator) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TRANSLATOR, translator);
        fragment.setArguments(bundle);

        return fragment;
    }

    protected void retrieveTranslation() {
        if (translator == null) {
            return;
        }
        translator.setFinished(false);

        translationTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                translator.retrieveTranslation(getActivity());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (!isCancelled()) {
                    super.onPostExecute(result);
                    showTranslation();
                }
                translator.setFinished(true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.noveogroup.vuplayer.BaseApplication;
import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.events.AddButtonClickEvent;
import com.noveogroup.vuplayer.translation.Translator;

public final class DetailedTranslationFragment extends AbstractTranslationFragment {

    private EditText textToTranslateView;
    private EditText sourceLanguageView;
    private EditText translationLanguageView;
    private TextView translationView;

    public static DetailedTranslationFragment newInstance(Translator translator) {
        DetailedTranslationFragment fragment = new DetailedTranslationFragment();
        return (DetailedTranslationFragment) initialize(fragment, translator);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
        translator = bundle != null ? (Translator) bundle.getParcelable(TRANSLATOR) : null;

        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        textToTranslateView = (EditText) view.findViewById(R.id.text_to_translate_view);
        sourceLanguageView = (EditText) view.findViewById(R.id.source_language_view);
        translationLanguageView = (EditText) view.findViewById(R.id.translation_language_view);
        translationView = (TextView) view.findViewById(R.id.translation_view);

        if (translator != null) {
            textToTranslateView.setText(translator.getText());
            sourceLanguageView.setText(translator.getSourceLanguage());
            translationLanguageView.setText(translator.getTranslationLanguage());

            if (!translator.isFinished()) {
                retrieveTranslation();
            } else {
                translationView.setText(translator.getDetailedTranslation(getActivity()));
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        textToTranslateView = null;
        sourceLanguageView = null;
        translationLanguageView = null;
        translationView = null;
    }

    @Override
    protected void showTranslation() {
        translationTask = null;
        translationView.setText(translator.getDetailedTranslation(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_detailed_translation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_translate:
                retrieveTranslation();
                return true;
            case R.id.action_add:
                if (textToTranslateView.getText() != null) {
                    BaseApplication.getEventBus().post(new AddButtonClickEvent(
                            textToTranslateView.getText().toString(),
                            translationView.getText().toString()));
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void retrieveTranslation() {
        if (translator == null) {
            return;
        }

        String text = textToTranslateView.getText().toString();
        String sourceLanguage = sourceLanguageView.getText().toString();
        String translationLanguage = translationLanguageView.getText().toString();
        if (!translator.getText().equals(text)
                || !translator.getSourceLanguage().equals(sourceLanguage)
                || !translator.getTranslationLanguage().equals(translationLanguage)) {

            translator.setText(text);
            translator.setSourceLanguage(sourceLanguage);
            translator.setTranslationLanguage(translationLanguage);
        }

        super.retrieveTranslation();
    }
}

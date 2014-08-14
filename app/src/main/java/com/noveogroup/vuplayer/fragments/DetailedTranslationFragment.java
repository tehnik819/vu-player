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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.noveogroup.vuplayer.BaseApplication;
import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.events.AddButtonClickEvent;
import com.noveogroup.vuplayer.events.TranslationPauseEvent;
import com.noveogroup.vuplayer.translation.Languages;
import com.noveogroup.vuplayer.translation.Translator;
import com.noveogroup.vuplayer.utils.ResourcesHandler;

public final class DetailedTranslationFragment extends AbstractTranslationFragment {

    Languages sourceLanguages;
    Languages translationLanguages;

    private EditText textToTranslateView;
    private Spinner sourceLanguageSpinner;
    private Spinner translationLanguageSpinner;
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
        sourceLanguageSpinner = (Spinner) view.findViewById(R.id.source_language_spinner);
        translationLanguageSpinner = (Spinner) view.findViewById(R.id.translation_language_spinner);
        translationView = (TextView) view.findViewById(R.id.translation_view);

        sourceLanguages = ResourcesHandler.getLanguages(getResources(),
                R.array.source_languages, "|");
        translationLanguages = ResourcesHandler.getLanguages(getResources(),
                R.array.translation_languages, "|");

        ArrayAdapter<String> sourceLanguagesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, sourceLanguages.languagesNamesFull);
        sourceLanguagesAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        sourceLanguageSpinner.setAdapter(sourceLanguagesAdapter);

        ArrayAdapter<String> translationLanguagesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, translationLanguages.languagesNamesFull);
        translationLanguagesAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        translationLanguageSpinner.setAdapter(sourceLanguagesAdapter);

        if (translator != null) {
            textToTranslateView.setText(translator.getText());
            sourceLanguageSpinner.setSelection(getLanguagePosition(
                    sourceLanguages.languagesNamesShort, translator.getSourceLanguage()));
            translationLanguageSpinner.setSelection(getLanguagePosition(
                    translationLanguages.languagesNamesShort, translator.getTranslationLanguage()));

            if (!translator.isFinished()) {
                retrieveTranslation();
            } else {
                translationView.setText(translator.getDetailedTranslation(getActivity()));
            }
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        String sourceLanguage = sourceLanguages
                .languagesNamesShort[sourceLanguageSpinner.getSelectedItemPosition()];
        String translationLanguage = translationLanguages
                .languagesNamesShort[translationLanguageSpinner.getSelectedItemPosition()];
        BaseApplication.getEventBus()
                .post(new TranslationPauseEvent(sourceLanguage, translationLanguage));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        textToTranslateView = null;
        sourceLanguageSpinner = null;
        translationLanguageSpinner = null;
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
        String sourceLanguage = sourceLanguages
                .languagesNamesShort[sourceLanguageSpinner.getSelectedItemPosition()];
        String translationLanguage = translationLanguages
                .languagesNamesShort[translationLanguageSpinner.getSelectedItemPosition()];
        if (!translator.getText().equals(text)
                || !translator.getSourceLanguage().equals(sourceLanguage)
                || !translator.getTranslationLanguage().equals(translationLanguage)) {

            translator.setText(text);
            translator.setSourceLanguage(sourceLanguage);
            translator.setTranslationLanguage(translationLanguage);
        }

        super.retrieveTranslation();
    }

    private int getLanguagePosition(String[] languagesNames, String name) {
        for (int i = 0; i < languagesNames.length; ++i) {
            if (languagesNames[i].equals(name)) {
                return i;
            }
        }

        return languagesNames.length;
    }
}

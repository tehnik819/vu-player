/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;

public final class DetailedTranslationFragment extends AbstractTranslationFragment {

    private EditText textToTranslateView;
    private EditText sourceLanguageView;
    private EditText translationLanguageView;
    private TextView translationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        retrieveArguments(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        textToTranslateView = (EditText) view.findViewById(R.id.text_to_translate_view);
        sourceLanguageView = (EditText) view.findViewById(R.id.source_language_view);
        translationLanguageView = (EditText) view.findViewById(R.id.translation_language_view);
        translationView = (TextView) view.findViewById(R.id.translation_view);

        if (translator != null) {
            textToTranslateView.setText(translator.getText());
            sourceLanguageView.setText(translator.getSourceLanguage());
            translationLanguageView.setText(translator.getTranslationLanguage());

            if (!isFinished) {
                retrieveTranslation();
            } else {
                translationView.setText(translator.getDetailedTranslation());
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
        translationView.setText(translator.getDetailedTranslation());
    }
}

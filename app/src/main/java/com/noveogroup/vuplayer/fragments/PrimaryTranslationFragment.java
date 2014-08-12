/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.translation.Translator;

public final class PrimaryTranslationFragment extends AbstractTranslationFragment {

    TextView sourceTextView;
    TextView resultTextView;
    ProgressBar progressBar;


    public static PrimaryTranslationFragment newInstance(Translator translator) {
        PrimaryTranslationFragment fragment = new PrimaryTranslationFragment();
        return (PrimaryTranslationFragment) initialize(fragment, translator);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
        translator = bundle != null ? (Translator) bundle.getParcelable(TRANSLATOR) : null;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_translation_dialog, null, false);
        builder.setView(view);

        sourceTextView = (TextView) view.findViewById(R.id.translation_dialog_source_text);
        resultTextView = (TextView) view.findViewById(R.id.translation_dialog_result_text);
        progressBar = (ProgressBar) view.findViewById(R.id.translation_dialog_progress_bar);

        builder.setPositiveButton(getResources()
                .getString(R.string.translation_dialog_more_button_text),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNeutralButton(getResources()
                .getString(R.string.translation_dialog_add_button_text),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(getResources()
                .getString(R.string.translation_dialog_cancel_button_text),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        if (translator != null) {
            sourceTextView.setText(translator.getText());
            if(!translator.isFinished()) {
                retrieveTranslation();
            } else {
                showTranslation();
            }
        }

        return dialog;
    }

    @Override
    protected void showTranslation() {
        translationTask = null;
        resultTextView.setText(translator.getPrimaryTranslation());
        progressBar.setVisibility(View.INVISIBLE);
        resultTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        sourceTextView = null;
        resultTextView = null;
        progressBar = null;
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.translation.Translator;

public class TranslationDialogFragment extends DialogFragment {

    private static final String TRANSLATOR = "VuPlayer.TranslationDialogFragment.TRANSLATOR";
    private static final String TEXT = "VuPlayer.TranslationDialogFragment.TEXT";

    private Translator translator;
    private String text;

    TextView sourceTextView;
    TextView resultTextView;
    ProgressBar progressBar;

    public static TranslationDialogFragment newInstance(String text, Translator translator){
        TranslationDialogFragment fragment = new TranslationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TEXT, text);
        bundle.putParcelable(TRANSLATOR, translator);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
        if (bundle != null) {
            text = bundle.getString(TEXT);
            translator = bundle.getParcelable(TRANSLATOR);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_translation_dialog, null, false);
        builder.setView(view);

        sourceTextView = (TextView) view.findViewById(R.id.translation_dialog_source_text);
        sourceTextView.setText(text);
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

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                translator.retrieveTranslation();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                showTranslation();
            }
        }.execute();

        AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        return dialog;
    }

    private void showTranslation() {
        resultTextView.setText(translator.getPrimaryTranslation());
        progressBar.setVisibility(View.INVISIBLE);
        resultTextView.setVisibility(View.VISIBLE);
    }
}

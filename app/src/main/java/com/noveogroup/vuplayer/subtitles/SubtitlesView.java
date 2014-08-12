/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.subtitles;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;

import java.util.ArrayList;

public class SubtitlesView extends TextView {

    protected static final String TAG = "VuPlayer.SubtitlesView";

    protected OnSubtitlesTouchListener onSubtitlesTouchListener;
    protected ArrayList<Selection> selections;
    SpannableString spannableText;

    protected class Selection {
        protected int startIndex;
        protected int endIndex;
        protected ForegroundColorSpan textColor;
        protected BackgroundColorSpan backgroundColor;
    }

    public interface OnSubtitlesTouchListener {
        void onSubtitlesTouch();
    }


    public SubtitlesView(Context context) {
        super(context);
    }

    public SubtitlesView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SubtitlesView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        String text = getText().toString();
        if (text.length() == 0) {
            return false;
        }

        if (selections == null) {
            selections = new ArrayList<Selection>();
            spannableText = new SpannableString(text);
        }

        onSubtitlesTouchListener.onSubtitlesTouch();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int[] wordIndices = expandToWord(getCharacterIndex(event));
            if(wordIndices == null) {
                return true;
            }

            for (Selection selection : selections) {
                if (selection.startIndex == wordIndices[0]) {
                    spannableText.removeSpan(selection.textColor);
                    spannableText.removeSpan(selection.backgroundColor);
                    selections.remove(selection);
                    setText(spannableText);
                    return true;
                }
            }

            Selection selection = new Selection();
            selection.startIndex = wordIndices[0];
            selection.endIndex = wordIndices[1];
            selection.textColor = new ForegroundColorSpan(getResources()
                    .getColor(R.color.subtitles_selected_text));
            selection.backgroundColor = new BackgroundColorSpan(getResources()
                    .getColor(R.color.subtitles_selected_background));

            selections.add(selection);

            spannableText.setSpan(selection.textColor, selection.startIndex,
                    selection.endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannableText.setSpan(selection.backgroundColor, selection.startIndex,
                    selection.endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            setText(spannableText);
        }

        return super.onTouchEvent(event);
    }

    public void setOnSubtitlesTouchListener(OnSubtitlesTouchListener listener) {
        onSubtitlesTouchListener = listener;
    }

    protected int getCharacterIndex(MotionEvent event) {
        Layout layout = getLayout();
        if(layout == null) {
            return -1;
        }

        int line = layout.getLineForVertical((int) event.getY());
        return layout.getOffsetForHorizontal(line, event.getX());
    }

    protected int[] expandToWord(int index) {
        int startIndex = index;
        int endIndex = index;
        String text = getText().toString();

        if (index < 0 || index > text.length() - 1 || !checkIfWordPart(text.charAt(index))) {
            return null;
        }

        while (startIndex > 0) {
            if (!checkIfWordPart(text.charAt(startIndex - 1))) {
                break;
            }
            --startIndex;
        }

        while (endIndex < text.length() - 1) {
            ++endIndex;
            if (!checkIfWordPart(text.charAt(endIndex))) {
                break;
            }
        }

        return new int[] {startIndex, endIndex};
    }

    protected boolean checkIfWordPart(char symbol) {
        return (!(symbol == ' ' || symbol == ',' || symbol == '.' || symbol == '?' || symbol == '!'
                || symbol == ':' || symbol == ';' || symbol == '\n'));
    }

    public String getSelectedText() {

        if(selections == null) {
            return "";
        }

        char[] text = getText().toString().toCharArray();
        char[] selectedText = new char[text.length];
        int n = 0;

        for (Selection selection : selections) {
            for (int i = selection.startIndex; i < selection.endIndex; ++i) {
                selectedText[n] = text[i];
                ++n;
            }
            selectedText[n] = ' ';
            ++n;
        }
        return new String(selectedText, 0, n - 1);
    }

    public void resetSelections() {
        selections = null;
        spannableText = null;
    }
}

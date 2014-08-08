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
import android.view.MotionEvent;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;

public class SubtitlesView extends TextView {

    protected static final String TAG = "VuPlayer.SubtitlesView";

    protected OnSubtitlesTouchListener onSubtitlesTouchListener;
    protected int selectionStartIndex = -1;
    protected int selectionEndIndex = -1;
//    protected int currentSelectionStartIndex;
//    protected int currentSelectionEndIndex;
    protected int currentInitialIndex;
    protected int currentFinalIndex;

    ForegroundColorSpan selectedTextColor = new ForegroundColorSpan(getResources().getColor(R.color.subtitles_selected_text));
    BackgroundColorSpan selectedBackgroundColor = new BackgroundColorSpan(getResources().getColor(R.color.subtitles_selected_background));

    public interface OnSubtitlesTouchListener {
        void onSubtitlesTouch();
    }


    public SubtitlesView(Context context) {
        super(context);
        initialize();
    }

    public SubtitlesView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize();
    }

    public SubtitlesView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initialize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        String text = getText().toString();
        if (text.length() == 0 || getCharacterIndex(event) < 0) {
            return false;
        }
        onSubtitlesTouchListener.onSubtitlesTouch();

        int action = event.getAction();
//        Log.d(TAG, String.format("index = %d", getCharacterIndex(event)));
        if (action == MotionEvent.ACTION_DOWN) {
//            Log.d(TAG, "Action down.");
            currentInitialIndex = currentFinalIndex = getCharacterIndex(event);
            if(selectionStartIndex < 0) {
                selectionStartIndex = selectionEndIndex = currentInitialIndex;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
//            Log.d(TAG, "Action move.");
            currentFinalIndex = getCharacterIndex(event);
            if (currentInitialIndex <= selectionStartIndex && currentFinalIndex >= selectionEndIndex) {
                selectionStartIndex = currentInitialIndex;
                selectionEndIndex = currentFinalIndex;
            } else if (currentInitialIndex >= selectionEndIndex && currentFinalIndex <= selectionStartIndex) {
                selectionStartIndex = currentFinalIndex;
                selectionEndIndex = currentInitialIndex;
            } else if (currentInitialIndex >= selectionStartIndex && currentInitialIndex <= selectionEndIndex) {
                if (currentFinalIndex < selectionStartIndex) {
                    selectionStartIndex = currentFinalIndex;
                } else if (currentFinalIndex > selectionEndIndex) {
                    selectionEndIndex = currentFinalIndex;
                }
            } else if (currentFinalIndex > selectionStartIndex && currentFinalIndex < selectionEndIndex) {
                if (currentInitialIndex < selectionStartIndex) {
                    selectionStartIndex = currentFinalIndex;
                } else if (currentInitialIndex > selectionEndIndex) {
                    selectionEndIndex = currentFinalIndex;
                }

                if (selectionStartIndex != selectionEndIndex) {
                    SpannableString spannableText = new SpannableString(getText());
                    spannableText.setSpan(selectedTextColor, selectionStartIndex, selectionEndIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableText.setSpan(selectedBackgroundColor, selectionStartIndex, selectionEndIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    setText(spannableText);
                }
            }
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

    protected void initialize() {

    }
}

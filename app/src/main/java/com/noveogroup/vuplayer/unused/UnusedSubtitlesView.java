/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.unused;

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

public class UnusedSubtitlesView extends TextView {

    protected static final String TAG = "VuPlayer.SubtitlesView";

    protected OnSubtitlesTouchListener onSubtitlesTouchListener;
    protected int selectionStartIndex = -1;
    protected int selectionEndIndex = -1;
    protected int currentInitialIndex;
    protected int currentFinalIndex;

    protected ForegroundColorSpan selectedTextColor = new ForegroundColorSpan(getResources()
            .getColor(R.color.subtitles_selected_text));
    protected BackgroundColorSpan selectedBackgroundColor = new BackgroundColorSpan(getResources()
            .getColor(R.color.subtitles_selected_background));

    protected ForegroundColorSpan proposedTextColor = new ForegroundColorSpan(getResources()
            .getColor(R.color.subtitles_selected_text));
    protected BackgroundColorSpan proposedBackgroundColor = new BackgroundColorSpan(getResources()
            .getColor(R.color.subtitles_selected_background));

    public interface OnSubtitlesTouchListener {
        void onSubtitlesTouch();
    }


    public UnusedSubtitlesView(Context context) {
        super(context);
    }

    public UnusedSubtitlesView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public UnusedSubtitlesView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        String text = getText().toString();
        if (text.length() == 0 || getCharacterIndex(event) < 0) {
            return false;
        }
        onSubtitlesTouchListener.onSubtitlesTouch();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            currentInitialIndex = currentFinalIndex = getCharacterIndex(event);
            if (selectionStartIndex < 0) {
                selectionStartIndex = selectionEndIndex = currentInitialIndex;
            }
        } else if (action == MotionEvent.ACTION_MOVE){
            currentFinalIndex = getCharacterIndex(event);
            int currentStartIndex = currentInitialIndex;
            int currentEndIndex = currentFinalIndex;
            if (currentStartIndex > currentEndIndex) {
                int temp = currentEndIndex;
                currentEndIndex = currentStartIndex;
                currentStartIndex = temp;
            }
            if(currentStartIndex != currentEndIndex) {
                SpannableString spannableText = new SpannableString(getText());
                spannableText.setSpan(proposedTextColor, currentStartIndex, currentEndIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                spannableText.setSpan(proposedBackgroundColor, currentStartIndex, currentEndIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                setText(spannableText);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            currentFinalIndex = getCharacterIndex(event);
            if (selectionStartIndex == selectionEndIndex) {
                if(currentInitialIndex < currentFinalIndex) {
                    selectionStartIndex = currentInitialIndex;
                    selectionEndIndex = currentFinalIndex;
                } else {
                    selectionStartIndex = currentFinalIndex;
                    selectionEndIndex = currentInitialIndex;
                }
            } else if (currentInitialIndex > selectionStartIndex
                    && currentInitialIndex < selectionEndIndex) {
                if (currentFinalIndex > selectionEndIndex) {
                    selectionEndIndex = currentFinalIndex;
                } else if (currentFinalIndex < selectionStartIndex) {
                    selectionStartIndex = currentFinalIndex;
                }
            } else if (currentInitialIndex <= selectionStartIndex) {
                if (currentFinalIndex <= selectionStartIndex) {
                    if (currentInitialIndex < currentFinalIndex) {
                        selectionStartIndex = currentInitialIndex;
                        selectionEndIndex = currentFinalIndex;
                    } else {
                        selectionStartIndex = currentFinalIndex;
                        selectionEndIndex = currentInitialIndex;
                    }
                } else if (currentFinalIndex < selectionEndIndex) {
                    selectionStartIndex = currentFinalIndex;
                } else {
                    selectionStartIndex = selectionEndIndex = -1;
                }
            } else {
                if (currentFinalIndex >= selectionEndIndex) {
                    if (currentInitialIndex < currentFinalIndex) {
                        selectionStartIndex = currentInitialIndex;
                        selectionEndIndex = currentFinalIndex;
                    } else {
                        selectionStartIndex = currentFinalIndex;
                        selectionEndIndex = currentInitialIndex;
                    }
                } else if (currentFinalIndex > selectionStartIndex) {
                    selectionEndIndex = currentFinalIndex;
                } else {
                    selectionStartIndex = selectionEndIndex = -1;
                }
            }

            if (selectionStartIndex != selectionEndIndex) {
                SpannableString spannableText = new SpannableString(getText());
                spannableText.removeSpan(proposedTextColor);
                spannableText.removeSpan(proposedBackgroundColor);
                spannableText.setSpan(selectedTextColor, selectionStartIndex, selectionEndIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                spannableText.setSpan(selectedBackgroundColor, selectionStartIndex,
                        selectionEndIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                setText(spannableText);
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
}

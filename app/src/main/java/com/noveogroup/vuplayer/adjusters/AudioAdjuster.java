/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.adjusters;

import android.media.AudioManager;

public class AudioAdjuster {

    private static AudioAdjuster audioAdjuster = null;
    private AudioManager audioManager;
    private int maxVolume;

    private AudioAdjuster(AudioManager audioManager) {
        this.audioManager = audioManager;
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public synchronized static AudioAdjuster getInstance(AudioManager audioManager) {
        return audioAdjuster = audioAdjuster == null ?
                new AudioAdjuster(audioManager) : audioAdjuster;
    }

    public float addVolume(float addition) {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int additionInt = (int)(maxVolume * addition);

        if(additionInt > 0) {
            currentVolume = currentVolume + additionInt > maxVolume ? maxVolume
                                                        : currentVolume + additionInt;
        }
        else {
            currentVolume = currentVolume + additionInt < 0 ? 0 : currentVolume + additionInt;
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);

        return currentVolume / (float) maxVolume;
    }

}

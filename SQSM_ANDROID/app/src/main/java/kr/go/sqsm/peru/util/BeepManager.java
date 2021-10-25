/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.go.sqsm.peru.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;

import kr.go.sqsm.peru.R;


/**
 * Manages beeps and vibrations
 */
public final class BeepManager implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = BeepManager.class.getSimpleName();

    private static final float BEEP_VOLUME = 1.0f;
    private static final long VIBRATE_DURATION = 200L;

    private final Activity activity;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;

    public BeepManager(Activity activity) {
        this.activity = activity;
        this.mediaPlayer = null;
        updatePrefs();
    }

    /**
     * The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
     * so we now play on the music stream.
     */
    synchronized void updatePrefs() {
        playBeep = shouldBeep(activity);
        vibrate = shouldVibrate(activity);
        if (playBeep && mediaPlayer == null) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = buildMediaPlayer(activity);
        }
    }

    /**
     * MediaPlayer start
     */
    public synchronized void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * Set Beep
     * @param activity Context
     */
    private static boolean shouldBeep(Context activity) {
        boolean shouldPlayBeep = true;//AppConfig.isSoundNotification(activity);
        if (shouldPlayBeep) {
            // See if sound settings overrides this
            AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                shouldPlayBeep = false;
            }
        }
        return shouldPlayBeep;
    }

    /**
     * Set Vibrate
     * @param activity Context
     */
    private static boolean shouldVibrate(Context activity) {
        boolean shouldVibrate = true;//AppConfig.isVibrationNotification(activity);
        boolean isSwitchVibrate = true;//AppConfig.isSwitchVibration(activity);
        if (!shouldVibrate && isSwitchVibrate) {
            // See if sound settings overrides this
            AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            if (audioService.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                shouldVibrate = true;
            }
        }
        return shouldVibrate;
    }

    /**
     * MediaPlayer Create
     * @param activity Context
     */
    private MediaPlayer buildMediaPlayer(Context activity) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.msg);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            mediaPlayer = null;
        }
        return mediaPlayer;
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     * @param mp MediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.seekTo(0);
    }

    /**
     * we are finished, so put up an appropriate error toast if required and finish
     * possibly media player error, so release and recreate
     * @param mp MediaPlayer
     * @param what Error code
     * @param extra
     */
    @Override
    public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            activity.finish();
        } else {
            mp.release();
            mediaPlayer = null;
            updatePrefs();
        }
        return true;
    }

}

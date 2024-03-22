package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

public class MediaPlayerManager {

    private MediaPlayer mediaPlayer;

    public MediaPlayerManager(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void resetMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void playMediaPlayer(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSoundEnabled = preferences.getBoolean("sound_enabled", true);
        MediaPlayer collisionSound = MediaPlayer.create(context, R.raw.hurt);
        collisionSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        if (isSoundEnabled) {
            collisionSound.start();
        };
    }

}

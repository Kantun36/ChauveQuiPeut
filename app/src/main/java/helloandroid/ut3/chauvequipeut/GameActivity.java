package helloandroid.ut3.chauvequipeut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import android.media.MediaPlayer;
import android.os.Bundle;

public class GameActivity extends Activity {

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSoundEnabled = preferences.getBoolean("sound_enabled", true);
        mediaPlayer = MediaPlayer.create(this, R.raw.batman);
        if (isSoundEnabled) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.setLooping(true);
            mediaPlayer.stop();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        GameView gameView = new GameView(this, mediaPlayer);
        setContentView(gameView);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


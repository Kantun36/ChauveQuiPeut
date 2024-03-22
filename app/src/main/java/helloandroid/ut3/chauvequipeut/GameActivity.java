package helloandroid.ut3.chauvequipeut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

        mediaPlayer = MediaPlayer.create(this, R.raw.batman);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();



        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Passer le lecteur de musique à GameView
        GameView gameView = new GameView(this, mediaPlayer);
        setContentView(gameView);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


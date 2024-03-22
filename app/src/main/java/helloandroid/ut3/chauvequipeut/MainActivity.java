package helloandroid.ut3.chauvequipeut;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.FirebaseApp;

import android.media.MediaPlayer;
public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSoundEnabled = preferences.getBoolean("sound_enabled", true);

        if (isSoundEnabled) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ascenseur);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }



        Button startButton = findViewById(R.id.startButton);
        Button optionButton = findViewById(R.id.optionButton);
        Button scoreboardButton = findViewById(R.id.scoreboardButton);
        Button quitButton = findViewById(R.id.quitButton);
        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this).load(R.drawable.chauvelogo).into(gifImageView);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });

        scoreboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Intent intent = new Intent(MainActivity.this, ScoreboardActivity.class);
                startActivity(intent);
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Règles et astuces du jeu");
        builder.setMessage("Règles et astuces du jeu : " +
                "- Appuyez sur les touches en bas à droite de l'écran pour faire monter ou descendre la chauve-souris\n" +
                "- Inclinez votre écran à gauche ou à droite pour déplacer la chauve-souris horizontalement \n" +
                "- Évitez les obstacles pour ne pas être éliminé \n" +
                "- Cachez le détecteur de lumière pour scanner les obstacles\n");
        builder.setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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


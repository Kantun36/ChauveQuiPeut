package helloandroid.ut3.chauvequipeut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class OptionActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private Switch switchSound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_option);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSoundEnabled = preferences.getBoolean("sound_enabled", true);

        if (isSoundEnabled) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ascenseur);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }


        Button quitButton = findViewById(R.id.quitButton);
        switchSound = findViewById(R.id.switchSound);
        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this).load(R.drawable.chauvelogo).into(gifImageView);



        switchSound.setChecked(isSoundEnabled);

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Enregistrer l'état de la préférence dans SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("sound_enabled", isChecked);
            editor.apply();

        });


        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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


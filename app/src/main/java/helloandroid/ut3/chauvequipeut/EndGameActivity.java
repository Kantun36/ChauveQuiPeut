package helloandroid.ut3.chauvequipeut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EndGameActivity extends AppCompatActivity {

    private TextView textViewScore;
    private Button buttonRestart;
    private Button buttonMenu;

    private MediaPlayer mediaPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        mediaPlayer = MediaPlayer.create(this, R.raw.ascenseur);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this).load(R.drawable.chauveperdu).into(gifImageView);


        // Initialisation des éléments de l'interface utilisateur
        textViewScore = findViewById(R.id.textViewScore);
        buttonRestart = findViewById(R.id.buttonRestart);
        buttonMenu = findViewById(R.id.buttonMenu);

        // Récupérer le score passé depuis l'activité précédente
        String score = getIntent().getStringExtra("score");
        textViewScore.setText("Temps effectué: " + score);

        // Ajouter un écouteur de clic pour le bouton "Recommencer"
        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Intent intent = new Intent(EndGameActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        // Ajouter un écouteur de clic pour le bouton "Retour au Menu"
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                // Action à effectuer lors du clic sur le bouton "Retour au Menu"
                Intent intent = new Intent(EndGameActivity.this, MainActivity.class);
                startActivity(intent);
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


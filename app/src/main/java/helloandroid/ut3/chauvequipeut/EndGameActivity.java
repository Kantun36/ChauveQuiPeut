package helloandroid.ut3.chauvequipeut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EndGameActivity extends AppCompatActivity {

    private TextView textViewScore;
    private Button buttonRestart;
    private Button buttonMenu;

    private MediaPlayer mediaPlayer;
    FirebaseFirestore db;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

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
                    mediaPlayer.stop();
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
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                // Action à effectuer lors du clic sur le bouton "Retour au Menu"
                Intent intent = new Intent(EndGameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        db = FirebaseFirestore.getInstance();

        // Créer un objet Reservation pour stocker les détails de la réservation
        Score reservation = new Score(score);

        // Ajouter la réservation à la collection appropriée dans Firestore
        db.collection("score")
                .add(reservation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
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


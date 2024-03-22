package helloandroid.ut3.chauvequipeut;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndGameActivity extends AppCompatActivity {

    private TextView textViewScore;
    private Button buttonRestart;
    private Button buttonMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        // Initialisation des éléments de l'interface utilisateur
        textViewScore = findViewById(R.id.textViewScore);
        buttonRestart = findViewById(R.id.buttonRestart);
        buttonMenu = findViewById(R.id.buttonMenu);

        // Récupérer le score passé depuis l'activité précédente
        int score = getIntent().getIntExtra("score", 0);
        textViewScore.setText("Score: " + score);

        // Ajouter un écouteur de clic pour le bouton "Recommencer"
        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action à effectuer lors du clic sur le bouton "Recommencer"
                restartGame();
            }
        });

        // Ajouter un écouteur de clic pour le bouton "Retour au Menu"
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action à effectuer lors du clic sur le bouton "Retour au Menu"
                returnToMenu();
            }
        });
    }

    private void restartGame() {
        // Mettez ici le code pour redémarrer le jeu
        // Par exemple, vous pouvez démarrer une nouvelle activité pour le jeu
    }

    private void returnToMenu() {
        // Mettez ici le code pour revenir au menu principal
        // Par exemple, vous pouvez démarrer une nouvelle activité pour le menu principal
    }
}


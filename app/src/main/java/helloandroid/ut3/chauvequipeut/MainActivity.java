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


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Supprimer la barre de titre de l'activité
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Définir la vue de jeu comme contenu principal
        setContentView(new GameView(this));

        // Mettre l'activité en mode plein écran
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
      
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button optionButton = findViewById(R.id.optionButton);
        Button quitButton = findViewById(R.id.quitButton);
        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this).load(R.drawable.chauvelogo).into(gifImageView);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Start button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, GameView.class);
                startActivity(intent);
            }
        });

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Option button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}


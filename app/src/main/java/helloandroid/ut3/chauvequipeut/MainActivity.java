package helloandroid.ut3.chauvequipeut;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

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
    }
}


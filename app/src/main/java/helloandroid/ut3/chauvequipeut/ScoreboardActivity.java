package helloandroid.ut3.chauvequipeut;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardActivity extends Activity {
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        RecyclerView recyclerView = findViewById(R.id.recycleReview);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scoreData(new ScoreCallback() {
            @Override
            public void onCallback(List<Score> restaurants) {
                ScoreAdapter adapter = new ScoreAdapter(ScoreboardActivity.this, restaurants);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public void scoreData(ScoreCallback callback) {
        CollectionReference scoreCollection = db.collection("score");
        List<Score> scores = new ArrayList<>();

        scoreCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String scoreId = document.getId();
                        String nom = document.getString("nom");
                        String score = document.getString("score");
                        scores.add(new Score(nom, score));

                        if (scores.size() == queryDocumentSnapshots.size()) {
                            callback.onCallback(scores);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("ScoreboardActivity", "Erreur lors de la récupération des données", e);
                });
    }
}

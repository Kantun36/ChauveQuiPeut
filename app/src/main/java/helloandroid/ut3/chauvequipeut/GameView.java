package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Random;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private final GameThread thread;
    private List<Obstacle> obstacles;
    private final Random random;

    private boolean touched;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        thread = new GameThread(getHolder(), this);
        obstacles = new ArrayList<>();
        touched = false;
        random = new Random();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        // Generate initial obstacles
        generateInitialObstacles();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touched = true;
        }
        return true;
    }

    public void update() {
        // Remove obstacles if needed
        // For simplicity, we won't remove obstacles in this example
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            // Draw the obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.draw(canvas);
            }
        }
    }

    private void generateInitialObstacles() {
        int obstacleCount = 4; // Nombre d'obstacles à générer initialement
        int obstacleWidth = getWidth() / 4; // Largeur de chaque obstacle

        // Facteur de variation pour la hauteur des obstacles
        float heightFactor = 0.5f; // Modifier selon vos préférences

        // Variables pour suivre les positions x précédemment générées
        Set<Float> topXPositions = new HashSet<>();
        Set<Float> bottomXPositions = new HashSet<>();

        for (int i = 0; i < obstacleCount; i++) {
            // Générer une hauteur d'obstacle aléatoire
            int obstacleHeight;

            // Générer une position x aléatoire
            float randomX = random.nextInt(getWidth() - obstacleWidth);

            // Déterminer si obstacle en haut ou en bas
            boolean top = (i % 2 == 0);

            // Vérifier si la position x générée se chevauche avec les obstacles précédents dans la même rangée
            if (top && !isOverlapping(topXPositions, randomX, obstacleWidth)) {
                // Si le triangle du haut et du bas se chevauchent en termes de position x, réduire la hauteur
                if (bottomXPositions.contains(randomX)) {
                    obstacleHeight = (int) (getHeight() * heightFactor * random.nextFloat() * 0.5); // Hauteur réduite
                } else {
                    obstacleHeight = (int) (getHeight() * heightFactor * random.nextFloat());
                }
                obstacles.add(new Obstacle(getContext(), randomX, 0, obstacleWidth, obstacleHeight, true));
                topXPositions.add(randomX);
            } else if (!top && !isOverlapping(bottomXPositions, randomX, obstacleWidth)) {
                // Si le triangle du haut et du bas se chevauchent en termes de position x, réduire la hauteur
                if (topXPositions.contains(randomX)) {
                    obstacleHeight = (int) (getHeight() * heightFactor * random.nextFloat() * 0.5); // Hauteur réduite
                } else {
                    obstacleHeight = (int) (getHeight() * heightFactor * random.nextFloat());
                }
                obstacles.add(new Obstacle(getContext(), randomX, getHeight() - obstacleHeight, obstacleWidth, obstacleHeight, false));
                bottomXPositions.add(randomX);
            }
        }
    }



    // Helper method to check if a new obstacle overlaps with previous obstacles
    private boolean isOverlapping(Set<Float> positions, float newX, float obstacleWidth) {
        for (float position : positions) {
            // Check if the new obstacle's x position is within the range of previous obstacles
            if (Math.abs(newX - position) < obstacleWidth) {
                return true; // Overlapping
            }
        }
        return false; // Not overlapping
    }


}


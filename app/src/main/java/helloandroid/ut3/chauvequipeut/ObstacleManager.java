package helloandroid.ut3.chauvequipeut;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ObstacleManager {

    private final Random random ;
    private Context context;
    private List<Obstacle> obstacles;

    private int viewWidth;

    private int viewHeight;

    public ObstacleManager(Context context, int viewWidth, int viewHeight) {
       obstacles = new ArrayList<>();
       this.context = context;
       random = new Random();
       this.viewWidth = viewWidth;
       this.viewHeight = viewHeight;
    }

    public void generateInitialObstacles() {
        int obstacleCount = 4; // Nombre d'obstacles à générer initialement
        int obstacleWidth = viewWidth / 4; // Largeur de chaque obstacle

        // Facteur de variation pour la hauteur des obstacles
        float heightFactor = 0.5f; // Modifier selon vos préférences

        // Variables pour suivre les positions x précédemment générées
        Set<Float> topXPositions = new HashSet<>();
        Set<Float> bottomXPositions = new HashSet<>();

        for (int i = 0; i < obstacleCount; i++) {
            // Générer une hauteur d'obstacle aléatoire
            int obstacleHeight;

            // Générer une position x aléatoire
            float randomX = random.nextInt(viewWidth - obstacleWidth);

            // Déterminer si obstacle en haut ou en bas
            boolean top = (i % 2 == 0);

            // Vérifier si la position x générée se chevauche avec les obstacles précédents dans la même rangée
            if (top && !isOverlapping(topXPositions, randomX, obstacleWidth)) {
                // Si le triangle du haut et du bas se chevauchent en termes de position x, réduire la hauteur
                if (bottomXPositions.contains(randomX)) {
                    obstacleHeight = (int) (viewHeight * heightFactor * random.nextFloat() * 0.5) /2; // Hauteur réduite
                } else {
                    obstacleHeight = (int) (viewHeight * heightFactor * random.nextFloat()) / 2;
                }
                obstacles.add(new Obstacle(context, randomX, 0, obstacleWidth, obstacleHeight, true));
                topXPositions.add(randomX);
            } else if (!top && !isOverlapping(bottomXPositions, randomX, obstacleWidth)) {
                // Si le triangle du haut et du bas se chevauchent en termes de position x, réduire la hauteur
                if (topXPositions.contains(randomX)) {
                    obstacleHeight = (int) (viewHeight * heightFactor * random.nextFloat() * 0.5)/2; // Hauteur réduite
                } else {
                    obstacleHeight = (int) (viewHeight * heightFactor * random.nextFloat())/2;
                }
                obstacles.add(new Obstacle(context, randomX, viewHeight- obstacleHeight, obstacleWidth, obstacleHeight, false));
                bottomXPositions.add(randomX);
            }
        }
    }


    public void updateObstacle() {
        for (Obstacle obstacle : obstacles) {
            obstacle.moveLeft(12);
        }
        if (obstacles.get(obstacles.size() - 1).getX() <= viewWidth * 0.75) {
            generateNewObstacle();
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

    private void generateNewObstacle() {
        int obstacleWidth = viewWidth / 4; // Largeur de chaque obstacle
        int obstacleHeight; // Hauteur de chaque obstacle
        int minHeight = viewHeight / 5; // Hauteur minimale

        // Facteur de variation pour la hauteur des obstacles
        float heightFactor = 0.5f; // Modifier selon vos préférences

        // Variables pour suivre les positions x précédemment générées
        Set<Float> topXPositions = new HashSet<>();
        Set<Float> bottomXPositions = new HashSet<>();

        // Générer une position x aléatoire avec un léger décalage
        float randomX = viewWidth + random.nextInt(viewWidth / 4); // Léger décalage supplémentaire

        // Déterminer si obstacle en haut ou en bas
        boolean top = random.nextBoolean();

        // Vérifier si la position x générée se chevauche avec les obstacles précédents dans la même rangée
        if (top) {
            if (!isOverlapping(topXPositions, randomX, obstacleWidth)) {
                // Calculer la hauteur de l'obstacle
                obstacleHeight = (int) (viewHeight * (heightFactor + random.nextFloat() * 0.4f));
                // Appliquer la hauteur minimale si nécessaire
                obstacleHeight = Math.max(obstacleHeight, minHeight);
                obstacles.add(new Obstacle(context, randomX, 0, obstacleWidth, obstacleHeight, true));
                topXPositions.add(randomX);
            }
        } else {
            if (!isOverlapping(bottomXPositions, randomX, obstacleWidth)) {
                // Calculer la hauteur de l'obstacle
                obstacleHeight = (int) (viewHeight * heightFactor * random.nextFloat());
                // Appliquer la hauteur minimale si nécessaire
                obstacleHeight = Math.max(obstacleHeight, minHeight);
                obstacles.add(new Obstacle(context, randomX, viewHeight - obstacleHeight, obstacleWidth, obstacleHeight, false));
                bottomXPositions.add(randomX);
            }
        }
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}

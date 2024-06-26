package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;


public class Obstacle {
    private final Paint paint;
    private final Path path;
    private float x, y;
    private final float width, height;
    private final boolean top;
    private final Paint strokePaint; // Ajout d'un Paint pour le contour


    public Obstacle(Context context, float x, float y, float width, float height, boolean top) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = top;

        paint = new Paint();
        paint.setColor(Color.argb(40, 0, 0, 0)); // Change la couleur en noir semi-transparent
        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE); // Définition du style de contour
        strokePaint.setStrokeWidth(5); // Définition de l'épaisseur du contour
        strokePaint.setColor(Color.argb(40, 0, 0, 0)); // Change la couleur en noir semi-transparent

        path = new Path();
        if (top) {
            path.moveTo(x, y);
            path.lineTo(x + width, y);
            path.lineTo(x + width / 2, y + height);
        } else {
            path.moveTo(x, y + height);
            path.lineTo(x + width / 2, y);
            path.lineTo(x + width, y + height);
        }
        path.close();
    }

    public void moveLeft(float distance) {
        x -= distance;
        updatePath();
    }

    private void updatePath() {
        path.reset();
        if (top) {
            path.moveTo(x, y);
            path.lineTo(x + width, y);
            path.lineTo(x + width / 2, y + height);
        } else {
            path.moveTo(x, y + height);
            path.lineTo(x + width / 2, y);
            path.lineTo(x + width, y + height);
        }
        path.close();
    }

    public void draw(Canvas canvas) {
        if (canvas != null) {
            // Dessiner l'obstacle
            canvas.drawPath(path, paint);
            canvas.drawPath(path, strokePaint);
        }
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }


    public float getWidth() {
        return width;
    }

    public boolean collidesWithCircle(float circleX, float circleY, float circleRadius) {
        // Calculer la distance entre le centre du cercle et le coin inférieur gauche de l'obstacle
        float distanceX = Math.abs(circleX - (x + width / 2));
        float distanceY = Math.abs(circleY - (y + height / 2));

        // Si la distance est inférieure au rayon du cercle, il y a collision
        if (distanceX > (width / 2 + circleRadius) || distanceY > (height / 2 + circleRadius)) {
            return false;
        }

        // Si la distance est inférieure à la somme des rayons, il y a collision
        if (distanceX <= (width / 2) || distanceY <= (height / 2)) {
            return true;
        }

        // Vérifier si la collision se produit sur les coins de l'obstacle
        double cornerDistanceSquared = Math.pow(distanceX - width / 2, 2) + Math.pow(distanceY - height / 2, 2);
        return cornerDistanceSquared <= Math.pow(circleRadius, 2);
    }

    public void setStrokeColor(int color) {
        strokePaint.setColor(color);
    }
    public Path getPath() {
        return path;
    }



    public float getHeight() {
        return height;
    }

    public float[][] getTrianglePoints() {
        float[][] points = new float[3][2]; // Tableau 2D pour stocker les coordonnées de chaque point (x, y)

        if (top) {
            // Point supérieur gauche
            points[0][0] = x;          // x du point supérieur gauche
            points[0][1] = y;          // y du point supérieur gauche

            // Point supérieur droit
            points[1][0] = x + width;  // x du point supérieur droit
            points[1][1] = y;          // y du point supérieur droit

            // Point inférieur centre
            points[2][0] = x + width / 2;  // x du point inférieur centre
            points[2][1] = y + height;     // y du point inférieur centre
        } else {
            // Point inférieur gauche
            points[0][0] = x;          // x du point inférieur gauche
            points[0][1] = y + height; // y du point inférieur gauche

            // Point supérieur centre
            points[2][0] = x + width / 2;  // x du point supérieur centre
            points[2][1] = y;               // y du point supérieur centre

            // Point inférieur droit
            points[1][0] = x + width;  // x du point inférieur droit
            points[1][1] = y + height; // y du point inférieur droit
        }

        return points;
    }

}

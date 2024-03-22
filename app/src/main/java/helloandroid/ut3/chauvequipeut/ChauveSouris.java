package helloandroid.ut3.chauvequipeut;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;

public class ChauveSouris {

    private AnimatedImageDrawable img = null;
    private int x, y;
    private int TailleLongueur, TailleLargeur;
    private double velocity = 0.0;

    private final Context mContext;

    private int tailleEcranX, tailleEcranY;

    public ChauveSouris(final Context c) {

        mContext = c;
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        tailleEcranX = metrics.widthPixels;
        tailleEcranY = metrics.heightPixels;

        // Set the initial size of the bat (you can adjust these values)
        TailleLongueur = (int) (metrics.heightPixels * 0.05); // Set bat height to 20% of screen height
        TailleLargeur = (int) (TailleLongueur * 793 / 446.0); // Maintain aspect ratio based on the original image size

        y = metrics.heightPixels / 2 - TailleLongueur / 2;
        x = metrics.widthPixels / 2 - TailleLargeur / 2;

        // Call resize method initially to set image dimensions
        resize(metrics.widthPixels, metrics.heightPixels);
    }

    public AnimatedImageDrawable setImage(final Context c, final int ressource, final int w, final int h) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return (AnimatedImageDrawable) c.getResources().getDrawable(ressource);
        }
        return null;
    }

    public void resize(int wScreen, int hScreen) {
        img = setImage(mContext, R.drawable.chauvesouris, TailleLargeur, TailleLongueur);
    }

    public void update() {
        // Implement update logic if needed (e.g., if bat should move)
        // For simplicity, we'll keep it empty for now
    }

    public void draw(Canvas canvas) {
        if (img == null) {
            return;
        }
        // Draw the current frame of the animation
        img.setBounds(x, y, x + TailleLargeur, y + TailleLongueur);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            img.start();
        }
        img.draw(canvas);
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public int getTailleLongueur() {
        return TailleLongueur;
    }
    public int getTailleLargeur() {
        return TailleLargeur;
    }

    public void moveUp() {
        y -= 20;
        if (y < 0) {
            y = 0;
        }
    }

    // Method to move the bat down
    public void moveDown() {
        y += 20;
        if (y + TailleLongueur > tailleEcranY) {
            y = tailleEcranY - TailleLongueur;
        }
    }

    // Method to reset bat movement
    public void resetMovement() {
    }

    public int[][] getCornerCoordinates() {
        int[][] corners = new int[4][2]; // Tableau 2D pour stocker les coordonnées de chaque coin (x, y)

        // Coin supérieur gauche
        corners[0][0] = x;          // x du coin supérieur gauche
        corners[0][1] = y;          // y du coin supérieur gauche

        // Coin supérieur droit
        corners[1][0] = x + TailleLargeur;  // x du coin supérieur droit
        corners[1][1] = y;                   // y du coin supérieur droit

        // Coin inférieur gauche
        corners[2][0] = x;          // x du coin inférieur gauche
        corners[2][1] = y + TailleLongueur;  // y du coin inférieur gauche

        // Coin inférieur droit
        corners[3][0] = x + TailleLargeur;  // x du coin inférieur droit
        corners[3][1] = y + TailleLongueur;  // y du coin inférieur droit

        return corners;
    }

}

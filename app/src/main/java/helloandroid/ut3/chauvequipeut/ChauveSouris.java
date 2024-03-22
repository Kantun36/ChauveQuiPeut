package helloandroid.ut3.chauvequipeut;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class ChauveSouris {

    private BitmapDrawable img = null;
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

    public BitmapDrawable setImage(final Context c, final int ressource, final int w, final int h) {
        Drawable dr = c.getResources().getDrawable(ressource);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        float scaleX = (float) w / bitmap.getWidth();
        float scaleY = (float) h / bitmap.getHeight();
        float scaleFactor = Math.min(scaleX, scaleY);

        return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scaleFactor), (int) (bitmap.getHeight() * scaleFactor), true));
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
        canvas.drawBitmap(img.getBitmap(), x, y, null);
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

}

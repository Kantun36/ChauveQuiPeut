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

    public Obstacle(Context context, float x, float y, float width, float height, boolean top) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = top;

        paint = new Paint();
        paint.setColor(Color.BLACK); // Change the color as needed

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
        }
    }

    public float getX() {
        return x;
    }

    public float getWidth() {
        return width;
    }
}

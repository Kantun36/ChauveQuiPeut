package helloandroid.ut3.chauvequipeut;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.content.Intent;

public class NightTimeManager {
    private boolean isNightTime;
    private float initialRadius;
    private float currentRadius;
    private float growthRate;

    public boolean isNightTime() {
        return isNightTime;
    }

    public float getInitialRadius() {
        return initialRadius;
    }

    public void setInitialRadius(float initialRadius) {
        this.initialRadius = initialRadius;
    }

    public float getCurrentRadius() {
        return currentRadius;
    }

    public void setCurrentRadius(float currentRadius) {
        this.currentRadius = currentRadius;
    }

    public float getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(float growthRate) {
        this.growthRate = growthRate;
    }

    public Paint getCirclePaint() {
        return circlePaint;
    }

    public void setCirclePaint(Paint circlePaint) {
        this.circlePaint = circlePaint;
    }

    private Paint circlePaint;
    private Context context;

    public NightTimeManager(Context context) {
        this.context = context;
        circlePaint = new Paint();
        circlePaint.setColor(Color.YELLOW);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5);
        // Initialize other necessary variables
    }

    public void drawNightMode(Canvas canvas, ChauveSouris chauveSouris, ObstacleManager obstacleManager) {
        if (isNightTime) {
            // Draw night mode components
            float centerX = chauveSouris.getX() + chauveSouris.getTailleLargeur() / 2;
            float centerY = chauveSouris.getY() + chauveSouris.getTailleLongueur() / 2;
            canvas.drawCircle(centerX, centerY, currentRadius, circlePaint);
            for (Obstacle obstacle : obstacleManager.getObstacles()) {
                if (obstacle.collidesWithCircle(centerX, centerY, currentRadius)) {
                    obstacle.setStrokeColor(Color.YELLOW);
                }
            }
            currentRadius *= growthRate;
            if (currentRadius > Math.max(canvas.getWidth(), canvas.getHeight())) {
                currentRadius = initialRadius;
            }
        } else {
            currentRadius = initialRadius;
        }
    }

    public void setNightTime(boolean isNightTime) {
        this.isNightTime = isNightTime;
    }
}

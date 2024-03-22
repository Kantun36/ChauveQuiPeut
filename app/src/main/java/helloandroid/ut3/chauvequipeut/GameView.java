package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
    private final GameThread thread;
    private ChauveSouris chauveSouris;
    private boolean touched;
    private float accelerationX;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        thread = new GameThread(getHolder(), this);

        // Initialize accelerometer
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize ChauveSouris object
        chauveSouris = new ChauveSouris(context);

        touched = false;
        accelerationX = 0.0f;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        // Register accelerometer listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
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
                // Unregister accelerometer listener
                sensorManager.unregisterListener(this);
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
            // Update ChauveSouris vertical position based on touch event
            chauveSouris.setY((int) event.getY());
        }
        return true;
    }

    public void update() {
        // Update ChauveSouris horizontal position based on accelerometer data
        chauveSouris.setX((int) (chauveSouris.getX() + accelerationX));
        // Move the ChauveSouris object vertically based on touch input
        // No need to do anything here as it's handled in onTouchEvent method
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            chauveSouris.draw(canvas); // Draw the ChauveSouris object
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Update accelerationX based on accelerometer data
            accelerationX = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
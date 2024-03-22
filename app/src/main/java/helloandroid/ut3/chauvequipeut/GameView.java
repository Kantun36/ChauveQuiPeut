package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener, View.OnTouchListener {
    private final GameThread thread;
    private ChauveSouris chauveSouris;
    private boolean touched;
    private float accelerationX;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private boolean isNightTime = false;

    private Bitmap upArrowBitmap;
    private Bitmap downArrowBitmap;
    private Rect upArrowRect;
    private Rect downArrowRect;
    private static final float ARROW_SCALE_FACTOR = 5f;
    private Handler moveHandler = new Handler();
    private boolean moveUpPressed = false;
    private boolean moveDownPressed = false;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setOnTouchListener(this); // Set onTouchListener for handling button clicks
        thread = new GameThread(getHolder(), this);

        // Initialize accelerometer
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Initialize ChauveSouris object
        chauveSouris = new ChauveSouris(context);

        touched = false;
        accelerationX = 0.0f;
        isNightTime = false;

        // Load arrow images
        upArrowBitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.arrow_up_float);
        downArrowBitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.arrow_down_float);
        upArrowBitmap = Bitmap.createScaledBitmap(upArrowBitmap, (int)(upArrowBitmap.getWidth() * ARROW_SCALE_FACTOR), (int)(upArrowBitmap.getHeight() * ARROW_SCALE_FACTOR), true);
        downArrowBitmap = Bitmap.createScaledBitmap(downArrowBitmap, (int)(downArrowBitmap.getWidth() * ARROW_SCALE_FACTOR), (int)(downArrowBitmap.getHeight() * ARROW_SCALE_FACTOR), true);
        upArrowRect = new Rect(0, 0, upArrowBitmap.getWidth(), upArrowBitmap.getHeight());
        downArrowRect = new Rect(0, getHeight() - downArrowBitmap.getHeight(), downArrowBitmap.getWidth(), getHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        // Register accelerometer listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        upArrowRect.offset(0, height - (downArrowBitmap.getHeight()*2));
        downArrowRect.offsetTo(0, height - downArrowBitmap.getHeight());
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

    public void update() {
        // Update ChauveSouris horizontal position based on accelerometer data
        chauveSouris.setX((int) (chauveSouris.getX() + (-accelerationX*3)));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            chauveSouris.draw(canvas);
            // Draw arrow buttons
            canvas.drawBitmap(upArrowBitmap, null, upArrowRect, null);
            canvas.drawBitmap(downArrowBitmap, null, downArrowRect, null);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Update accelerationX based on accelerometer data
            accelerationX = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            Log.d("LUMI", "LUMI : "+ lux);
            if(lux < 30){
                isNightTime = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Check if touch event is inside the up or down arrow button
                if (upArrowRect.contains((int) event.getX(), (int) event.getY())) {
                    // Move chauveSouris up
                    chauveSouris.moveUp();
                    // Start a timer to continue moving while button is held
                    startMoveTimer(true);
                } else if (downArrowRect.contains((int) event.getX(), (int) event.getY())) {
                    // Move chauveSouris down
                    chauveSouris.moveDown();
                    // Start a timer to continue moving while button is held
                    startMoveTimer(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                // Reset chauveSouris movement when touch is released
                chauveSouris.resetMovement();
                // Stop the timer
                stopMoveTimer();
                break;
        }
        return true;
    }

    private void startMoveTimer(final boolean moveUp) {
        if (moveUp) {
            moveUpPressed = true;
        } else {
            moveDownPressed = true;
        }
        moveHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (moveUpPressed) {
                    chauveSouris.moveUp();
                    startMoveTimer(true);
                } else if (moveDownPressed) {
                    chauveSouris.moveDown();
                    startMoveTimer(false);
                }
            }
        }, 20);
    }

    private void stopMoveTimer() {
        moveUpPressed = false;
        moveDownPressed = false;
        moveHandler.removeCallbacksAndMessages(null);
    }

}

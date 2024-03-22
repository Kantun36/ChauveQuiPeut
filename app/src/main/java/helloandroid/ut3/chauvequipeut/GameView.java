package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;

import android.preference.PreferenceManager;

import android.os.VibrationEffect;
import android.os.Vibrator;

import android.util.Log;
import android.os.Handler;
import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Random;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import android.view.View;
import android.media.MediaPlayer;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener, View.OnTouchListener {

    private ObstacleManager obstacleManager;
    private MediaPlayerManager mediaPlayerManager;

    private VibrationManager vibrationManager;

    private  NightTimeManager nightTimeManager;
    private final GameThread thread;
    private final Random random;
    private Bitmap background;
    private Bitmap scaled;
    private float accelerationX;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private Bitmap upArrowBitmap;
    private Bitmap downArrowBitmap;
    private Rect upArrowRect;
    private Rect downArrowRect;
    private Handler moveHandler = new Handler();
    private boolean moveUpPressed = false;
    private boolean moveDownPressed = false;
    private ChauveSouris chauveSouris;
    private Paint textPaint;
    private long startTimeMillis;
    private long elapsedTimeMillis;
    private boolean stopped = false;
    private String time;


    public GameView(Context context, MediaPlayer mediaPlayer) {
        super(context);
        mediaPlayerManager = new MediaPlayerManager(mediaPlayer);
        getHolder().addCallback(this);
        setFocusable(true);
        setOnTouchListener(this); // Set onTouchListener for handling button clicks
        thread = new GameThread(getHolder(), this);

        random = new Random();

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        startTimeMillis = System.currentTimeMillis();

        // Initialize accelerometer
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        vibrationManager = new VibrationManager((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE));

        // Initialize ChauveSouris object
        chauveSouris = new ChauveSouris(context);

        accelerationX = 0.0f;
        nightTimeManager = new NightTimeManager(context);
        nightTimeManager.setNightTime(false);

        // Charger les images des flèches
        upArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.uparrow);
        downArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.downarrow);

// Redimensionner les images avec un facteur d'échelle
        float scaleFactor = 0.2f; // Facteur d'échelle pour réduire la taille à la moitié
        upArrowBitmap = Bitmap.createScaledBitmap(upArrowBitmap, (int)(upArrowBitmap.getWidth() * scaleFactor), (int)(upArrowBitmap.getHeight() * scaleFactor), true);
        downArrowBitmap = Bitmap.createScaledBitmap(downArrowBitmap, (int)(downArrowBitmap.getWidth() * scaleFactor), (int)(downArrowBitmap.getHeight() * scaleFactor), true);

// Définir les zones de dessin des flèches
        upArrowRect = new Rect(0, 0, upArrowBitmap.getWidth(), upArrowBitmap.getHeight());
        downArrowRect = new Rect(0, getHeight() - downArrowBitmap.getHeight(), downArrowBitmap.getWidth(), getHeight());

        background = BitmapFactory.decodeResource(getResources(),R.drawable.cavebackground);

        nightTimeManager.setInitialRadius(chauveSouris.getTailleLongueur());
        nightTimeManager.setGrowthRate(1.1f);
        nightTimeManager.setCurrentRadius(nightTimeManager.getInitialRadius());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        // Generate initial obstacles
        // Register accelerometer listener
        obstacleManager = new ObstacleManager(getContext(), getWidth(), getHeight());
        obstacleManager.generateInitialObstacles();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        upArrowRect.offset(0, height - (downArrowBitmap.getHeight()*2));
        downArrowRect.offsetTo(0, height - downArrowBitmap.getHeight());
        scaled = Bitmap.createScaledBitmap(background, background.getWidth(),getHeight() , true);
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
        // Update ChauveSouris horizontal position based on accelerometer data with boundary check
        float newX = chauveSouris.getX() + (-accelerationX * 3);

        if (newX < 0) {
            newX = 0;
        } else if (newX + chauveSouris.getTailleLargeur() > getWidth()) {
            newX = getWidth() - chauveSouris.getTailleLargeur(); // Clamp to right edge
        }
        chauveSouris.setX((int) newX);

        obstacleManager.updateObstacle();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        int seconds = (int) (elapsedTimeMillis / 1000) % 60;
        int minutes = (int) ((elapsedTimeMillis / (1000*60)) % 60);



        if (canvas != null && !stopped) {
            canvas.drawBitmap(scaled,-200,0,null);
            // Draw the obstacles
            for (Obstacle obstacle : obstacleManager.getObstacles()) {
                obstacle.draw(canvas);
                int[][] bat = chauveSouris.getCornerCoordinates();
                float[][] ob = obstacle.getTrianglePoints();
                if(CollisionManager.checkForCollision(bat, ob)){
                    mediaPlayerManager.resetMediaPlayer();
                    // Faire vibrer le téléphone

                    vibrationManager.vibrate();

                    mediaPlayerManager.playMediaPlayer(getContext());

                    Intent intent = new Intent(getContext(), EndGameActivity.class);
                    intent.putExtra("score", time); // Passer le score
                    getContext().startActivity(intent);
                    mediaPlayerManager.resetMediaPlayer();
                    break;
                }
            }            
            chauveSouris.draw(canvas);
            // Draw arrow buttons
            canvas.drawBitmap(upArrowBitmap, null, upArrowRect, null);
            canvas.drawBitmap(downArrowBitmap, null, downArrowRect, null);

            // Dessiner le cercle supplémentaire lorsque c'est la nuit
            nightTimeManager.drawNightMode(canvas, chauveSouris, obstacleManager);

            chauveSouris.draw(canvas); // Dessiner la chauve-souris
            String timeString = String.format("%02d:%02d",  minutes, seconds);
            canvas.drawText(timeString, getWidth()/2-70, 100, textPaint);
            time = timeString;
        }

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Update accelerationX based on accelerometer data
            accelerationX = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            if(lux < 30){
                nightTimeManager.setNightTime(true);
            }else {
                nightTimeManager.setNightTime(false);
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
        }, 10);
    }

    private void stopMoveTimer() {
        moveUpPressed = false;
        moveDownPressed = false;
        moveHandler.removeCallbacksAndMessages(null);
    }

}


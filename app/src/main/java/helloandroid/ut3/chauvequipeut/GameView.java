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

import java.util.HashSet;
import java.util.Random;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener, View.OnTouchListener {
    private final GameThread thread;
    private List<Obstacle> obstacles;
    private final Random random;

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
        obstacles = new ArrayList<>();
        touched = false;
        random = new Random();


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
        // Generate initial obstacles
        generateInitialObstacles();
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
            // Draw the obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.draw(canvas);
            }            
            chauveSouris.draw(canvas);
            // Draw arrow buttons
            canvas.drawBitmap(upArrowBitmap, null, upArrowRect, null);
            canvas.drawBitmap(downArrowBitmap, null, downArrowRect, null);
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


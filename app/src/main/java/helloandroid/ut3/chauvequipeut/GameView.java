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
    private final GameThread thread;
    private MediaPlayer mediaPlayer;
    private List<Obstacle> obstacles;
    private final Random random;
    private Bitmap background;
    private Bitmap scaled;
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
    private ChauveSouris chauveSouris;
    // Ajouter un membre pour le rayon initial du cercle
    private float initialRadius;
    // Ajouter un membre pour le taux d'augmentation du rayon
    private float growthRate;
    // Ajouter un membre pour le rayon actuel du cercle
    private float currentRadius;

    private Paint textPaint;
    private long startTimeMillis;
    private long elapsedTimeMillis;

    private boolean stopped = false;

    private String time;


    public GameView(Context context, MediaPlayer mediaPlayer) {
        super(context);
        this.mediaPlayer = mediaPlayer;
        getHolder().addCallback(this);
        setFocusable(true);
        setOnTouchListener(this); // Set onTouchListener for handling button clicks
        thread = new GameThread(getHolder(), this);
        obstacles = new ArrayList<>();
        touched = false;
        random = new Random();

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        startTimeMillis = System.currentTimeMillis();

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

        background = BitmapFactory.decodeResource(getResources(),R.drawable.cavebackground);

        initialRadius = chauveSouris.getTailleLongueur();
        // Initialiser le taux d'augmentation du rayon
        growthRate = 1.1f;
        // Initialiser le rayon actuel
        currentRadius = initialRadius;
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
        // Déplacer les obstacles vers la gauche
        for (Obstacle obstacle : obstacles) {
            obstacle.moveLeft(5); // Déplacer de 5 pixels vers la gauche (ajuster selon votre besoin)
        }

        // Vérifier si un nouveau obstacle doit être généré
        if (obstacles.get(obstacles.size() - 1).getX() <= getWidth() * 0.75) {
            generateNewObstacle();
        }

    }
    private void generateNewObstacle() {
        int obstacleWidth = getWidth() / 4; // Largeur de chaque obstacle
        int obstacleHeight; // Hauteur de chaque obstacle
        int minHeight = getHeight() / 5; // Hauteur minimale

        // Facteur de variation pour la hauteur des obstacles
        float heightFactor = 0.5f; // Modifier selon vos préférences

        // Variables pour suivre les positions x précédemment générées
        Set<Float> topXPositions = new HashSet<>();
        Set<Float> bottomXPositions = new HashSet<>();

        // Générer une position x aléatoire avec un léger décalage
        float randomX = getWidth() + random.nextInt(getWidth() / 4); // Léger décalage supplémentaire

        // Déterminer si obstacle en haut ou en bas
        boolean top = random.nextBoolean();

        // Vérifier si la position x générée se chevauche avec les obstacles précédents dans la même rangée
        if (top) {
            if (!isOverlapping(topXPositions, randomX, obstacleWidth)) {
                // Calculer la hauteur de l'obstacle
                obstacleHeight = (int) (getHeight() * (heightFactor + random.nextFloat() * 0.4f));
                // Appliquer la hauteur minimale si nécessaire
                obstacleHeight = Math.max(obstacleHeight, minHeight);
                obstacles.add(new Obstacle(getContext(), randomX, 0, obstacleWidth, obstacleHeight, true));
                topXPositions.add(randomX);
            }
        } else {
            if (!isOverlapping(bottomXPositions, randomX, obstacleWidth)) {
                // Calculer la hauteur de l'obstacle
                obstacleHeight = (int) (getHeight() * heightFactor * random.nextFloat());
                // Appliquer la hauteur minimale si nécessaire
                obstacleHeight = Math.max(obstacleHeight, minHeight);
                obstacles.add(new Obstacle(getContext(), randomX, getHeight() - obstacleHeight, obstacleWidth, obstacleHeight, false));
                bottomXPositions.add(randomX);
            }
        }
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
            for (Obstacle obstacle : obstacles) {
                obstacle.draw(canvas);
                int[][] bat = chauveSouris.getCornerCoordinates();
                float[][] ob = obstacle.getTrianglePoints();
                if(intersects(new PointF(bat[0][0],bat[0][1]), new PointF(bat[1][0],bat[1][1]),
                        new PointF(ob[0][0],ob[0][1]), new PointF(ob[2][0],ob[2][1])) ||

                        intersects(new PointF(bat[1][0],bat[1][1]), new PointF(bat[3][0],bat[3][1]),
                                new PointF(ob[0][0],ob[0][1]), new PointF(ob[2][0],ob[2][1])) ||

                        intersects(new PointF(bat[0][0],bat[0][1]), new PointF(bat[2][0],bat[2][1]),
                                new PointF(ob[1][0],ob[1][1]), new PointF(ob[2][0],ob[2][1])) ||

                        intersects(new PointF(bat[2][0],bat[2][1]), new PointF(bat[3][0],bat[3][1]),
                                new PointF(ob[1][0],ob[1][1]), new PointF(ob[2][0],ob[2][1]))
                ){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Log.d("COLLISION" , "COLL");

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                    boolean isSoundEnabled = preferences.getBoolean("sound_enabled", true);
                    MediaPlayer collisionSound = MediaPlayer.create(this.getContext(), R.raw.hurt);
                    collisionSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                    if (isSoundEnabled) {
                        collisionSound.start();
                    };
                    Intent intent = new Intent(getContext(), EndGameActivity.class);
                    intent.putExtra("score", time); // Passer le score
                    getContext().startActivity(intent);

                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    break;
                    
                }
            }            
            chauveSouris.draw(canvas);
            // Draw arrow buttons
            canvas.drawBitmap(upArrowBitmap, null, upArrowRect, null);
            canvas.drawBitmap(downArrowBitmap, null, downArrowRect, null);

            // Dessiner le cercle supplémentaire lorsque c'est la nuit
            if (isNightTime) {
                Paint circlePaint = new Paint();
                circlePaint.setColor(Color.YELLOW); // Couleur du cercle
                circlePaint.setStyle(Paint.Style.STROKE);
                circlePaint.setStrokeWidth(5);

                // Calculer le centre de la chauve-souris
                float centerX = chauveSouris.getX() + chauveSouris.getTailleLargeur() / 2;
                float centerY = chauveSouris.getY() + chauveSouris.getTailleLongueur() / 2;

                // Dessiner le cercle concentrique autour de la chauve-souris
                canvas.drawCircle(centerX, centerY, currentRadius, circlePaint);

                // Augmenter le rayon actuel pour la prochaine mise à jour
                currentRadius *= growthRate;

                // Vérifier si le cercle a dépassé la taille de l'écran
                if (currentRadius > Math.max(canvas.getWidth(), canvas.getHeight())) {
                    // Si oui, réinitialiser le rayon pour la prochaine fois
                    currentRadius = initialRadius;
                }
            } else {
                // Si c'est le jour, réinitialiser le rayon du cercle
                currentRadius = initialRadius;
            }

            chauveSouris.draw(canvas); // Dessiner la chauve-souris
            String timeString = String.format("%02d:%02d",  minutes, seconds);
            canvas.drawText(timeString, getWidth()/2-70, 100, textPaint);
            time = timeString;
        }

    }




    static boolean intersects(PointF a1, PointF a2, PointF b1, PointF b2) {
        PointF intersection = new PointF();

        PointF b = new PointF(a2.x - a1.x, a2.y - a1.y);
        PointF d = new PointF(b2.x - b1.x, b2.y - b1.y);
        float bDotDPerp = b.x * d.y - b.y * d.x;

        // if b dot d == 0, it means the lines are parallel so have infinite intersection points
        if (bDotDPerp == 0)
            return false;

        PointF c = new PointF(b1.x - a1.x, b1.y - a1.y);
        float t = (c.x * d.y - c.y * d.x) / bDotDPerp;
        if (t < 0 || t > 1)
            return false;

        float u = (c.x * b.y - c.y * b.x) / bDotDPerp;
        if (u < 0 || u > 1)
            return false;

        intersection.set(a1.x + b.x * t, a1.y + b.y * t);

        return true;
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
            }else {
                isNightTime = false;
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


package helloandroid.ut3.chauvequipeut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private final GameThread thread;
    private int ballX, ballY;
    private int speedX, speedY;
    private boolean touched;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        thread = new GameThread(getHolder(), this);

        // Initialize ball position at the center of the screen
        ballX = getWidth() / 2;
        ballY = getHeight() / 2;

        // Initialize ball speed
        speedX = 5;
        speedY = 5;

        touched = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Change the direction of the ball and increase its speed when touched
            speedX *= -1;
            speedY *= -1;
            touched = true;
        }
        return true;
    }

    public void update() {
        // Move the ball
        ballX += speedX;
        ballY += speedY;

        // Reverse direction if the ball reaches the edge of the screen
        if (ballX <= 0 || ballX >= getWidth()) {
            speedX *= -1;
        }
        if (ballY <= 0 || ballY >= getHeight()) {
            speedY *= -1;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(250, 0, 0));
            canvas.drawCircle(ballX, ballY, 50, paint); // Draw the ball at the current position
        }
    }
}
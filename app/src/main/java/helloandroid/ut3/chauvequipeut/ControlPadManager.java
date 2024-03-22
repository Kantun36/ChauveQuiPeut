package helloandroid.ut3.chauvequipeut;

import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;


public class ControlPadManager implements View.OnTouchListener {

    private Rect upArrowRect;

    public Rect getUpArrowRect() {
        return upArrowRect;
    }

    public void setUpArrowRect(Rect upArrowRect) {
        this.upArrowRect = upArrowRect;
    }

    public Rect getDownArrowRect() {
        return downArrowRect;
    }

    public void setDownArrowRect(Rect downArrowRect) {
        this.downArrowRect = downArrowRect;
    }

    private Rect downArrowRect;
    private boolean moveUpPressed = false;
    private boolean moveDownPressed = false;
    private Handler moveHandler = new Handler();
    private ChauveSouris chauveSouris;

    public ControlPadManager(Rect upArrowRect, Rect downArrowRect, ChauveSouris chauveSouris) {
        this.upArrowRect = upArrowRect;
        this.downArrowRect = downArrowRect;
        this.chauveSouris = chauveSouris;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (upArrowRect.contains((int) event.getX(), (int) event.getY())) {
                    chauveSouris.moveUp();
                    startMoveTimer(true);
                } else if (downArrowRect.contains((int) event.getX(), (int) event.getY())) {
                    chauveSouris.moveDown();
                    startMoveTimer(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                chauveSouris.resetMovement();
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

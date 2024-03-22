package helloandroid.ut3.chauvequipeut;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

class GameThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean running;

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long lastUpdateTime = System.nanoTime();
        long targetTime = 1000000000 / 60; // 1/60th of a second in nanoseconds

        while (running) {
            Canvas canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    if (canvas != null) {
                        long now = System.nanoTime();
                        long elapsedTime = now - lastUpdateTime;

                        // If at least 1/60th of a second has passed since the last update and draw
                        if (elapsedTime >= targetTime) {
                            lastUpdateTime = now;

                            gameView.update();
                            gameView.draw(canvas);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Sleep to ensure that the loop runs approximately once every 1/60th of a second
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
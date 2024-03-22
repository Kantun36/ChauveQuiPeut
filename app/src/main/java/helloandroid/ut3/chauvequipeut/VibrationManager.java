package helloandroid.ut3.chauvequipeut;

import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationManager {

    private Vibrator vibrator;

    public VibrationManager(Vibrator vibrator) {
        this.vibrator = vibrator;
    }

    public void vibrate(){
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}

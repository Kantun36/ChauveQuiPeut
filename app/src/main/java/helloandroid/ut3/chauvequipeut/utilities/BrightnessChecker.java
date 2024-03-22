package helloandroid.ut3.chauvequipeut.utilities;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BrightnessChecker extends AppCompatActivity implements SensorEventListener{

    // LIGHT SENSOR
    private SensorManager sensorManager;
    private Sensor lightSensor;

    public void checkBrightnessAsync(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            Log.d("LUMI1", "LUMI111111111111 : "+ lux);
            lux = (lux - 35) * 255 / 35;
            // Faire quelque chose avec la valeur de luminosité (lux).
            Log.d("LUMI", "LUMI : "+ lux);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

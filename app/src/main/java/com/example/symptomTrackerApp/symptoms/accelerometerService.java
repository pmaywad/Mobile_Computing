package com.example.symptomTrackerApp.symptoms;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;


public class accelerometerService extends Service implements SensorEventListener {

    private SensorManager accelerometerManager;
    private Sensor accelerometerSensor;
    private final ArrayList<Integer> accelerometerXVals = new ArrayList<>();
    private final ArrayList<Integer> accelerometerYVals = new ArrayList<>();
    private final ArrayList<Integer> accelerometerZVals = new ArrayList<>();

    @Override
    public void onCreate(){

        accelerometerManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = accelerometerManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accelerometerXVals.clear();
        accelerometerYVals.clear();
        accelerometerZVals.clear();
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor genericSensor = sensorEvent.sensor;
        if (genericSensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            accelerometerXVals.add((int)(sensorEvent.values[0] * 100));
            accelerometerYVals.add((int)(sensorEvent.values[1] * 100));
            accelerometerZVals.add((int)(sensorEvent.values[2] * 100));

            if(accelerometerXVals.size() >= 230){
                stopSelf();
            }
        }
    }

    @Override
    public void onDestroy(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                accelerometerManager.unregisterListener(accelerometerService.this);

                Intent intent = new Intent("broadcastingAccelData");
                Bundle b = new Bundle();
                b.putIntegerArrayList("accelerometerXVals", accelerometerXVals);
                intent.putExtras(b);
                LocalBroadcastManager.getInstance(accelerometerService.this).sendBroadcast(intent);
            }
        });
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

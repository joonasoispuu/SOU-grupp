package com.example.workoutappgroupproject.sensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.example.workoutappgroupproject.R;

public class ProximityListener implements SensorEventListener {

    private static final String TAG = ProximityListener.class.getSimpleName();
    private static ToneGenerator toneGenerator;
    private static boolean hasTicked = false;
    Context application;
    Sensor proximitySensor;
    View view;
    boolean running;

    Listener mListener;

    public void stopSelf() {
        running = false;
    }

    public interface Listener {
        void onTick();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public ProximityListener(Context application, View view, Sensor proximitySensor) {
        this.application = application;
        this.proximitySensor = proximitySensor;
        this.view = view;
        setOnClickListener();
        running = true;
    }

    private void setOnClickListener() {
        view.findViewById(R.id.mainView).setOnClickListener(view1 -> {
            if (running) {
                doTick();
                onTickDone();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0]<proximitySensor.getMaximumRange()) {
            doTick();
        } else {
            onTickDone();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void doTick() {
        if (!hasTicked) {
            playTone(application.getApplicationContext(), ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
            mListener.onTick();
        }
        hasTicked = true;
    }

    private void onTickDone() {
        if (hasTicked) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // after 1/4 seconds
                    hasTicked = false;
                }
            }, 0);
        }
    }

    private static void playTone(Context context, int mediaFileRawId) {
        Log.d(TAG, "playTone");
        try {
            if (toneGenerator == null) {
                toneGenerator = new ToneGenerator(AudioManager.STREAM_RING, 100);
            }
            toneGenerator.startTone(mediaFileRawId, 200);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (toneGenerator != null) {
                        Log.d(TAG, "ToneGenerator released");
                        toneGenerator.release();
                        toneGenerator = null;
                    }
                }

            }, 200);
        } catch (Exception e) {
            Log.d(TAG, "Exception while playing sound:" + e);
        }
    }
}

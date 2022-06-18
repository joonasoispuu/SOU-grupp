package com.example.workoutappgroupproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.example.workoutappgroupproject.R;

import java.util.Locale;

public class BreakActivity extends AppCompatActivity {

    ActionBar actionBar;
    private void setActionBarColor(int color) {
        ColorDrawable colorDrawable
                = new ColorDrawable(color);
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    long time_data = -1;
    int quantity_data = -1;
    private static final int RESULT_NOT_SUCCESS = 200;
    public static final int RESULT_SUCCESS = 100;
    private long mtimeStartinMilliseconds;
    private long mtimeLeftinMilliseconds = -1;
    long timeVar;
    long time = 0;
    private CountDownTimer countDownTimer;
    TextView timeCounter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("time_data",time_data);
        outState.putInt("quantity_data",quantity_data);
        outState.putLong("time",timeVar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            int color = getResources().getColor(R.color.purple_500);
            setActionBarColor(color);
        }

        if (savedInstanceState != null) {
            time = savedInstanceState.getLong("time");
        } else {
            Intent intent = getIntent();
            int myTime = (int) intent.getExtras().getLong("time_data");
            if (myTime < 1) {
                time = 10;
            } else {
                time = myTime;
            }
//            time = 5;
        }
        if (time_data == -1 && quantity_data == -1) {
            Intent intent = getIntent();
            if (intent != null) {
                Bundle b = getIntent().getExtras();
                if(b != null){
                    time_data = b.getLong("time_data");
                    quantity_data = b.getInt("quantity_data");
                }
                System.out.println(time_data);
                System.out.println(quantity_data);
            }
        }

        timeCounter = findViewById(R.id.timeCounter);
        timeCounter.setText(String.valueOf(time));
        long milliseconds = (time+1) * 1000;
        setTime(milliseconds);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finished(-999);
//        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        System.out.println("on destroy!");
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    private void setTime(long milliseconds) {
        mtimeStartinMilliseconds = milliseconds;
        resetTimer();
        startTimer();
    }

    private void resetTimer(){
        mtimeLeftinMilliseconds = mtimeStartinMilliseconds;
        updateTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(mtimeLeftinMilliseconds, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mtimeLeftinMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish(){
                finished(RESULT_SUCCESS);
            }
        }.start();
    }


    private void finished(int RESULT) {
        // cancel timer
        if(countDownTimer!=null){
            countDownTimer.cancel();
        } else {
            System.out.println("can't cancel timer!");
        }

        int seconds = (int) (mtimeLeftinMilliseconds / 1000) % 60;
        // cancel new instance creation (complete)
        Intent data = new Intent();
        // got id from intent getIntExtra()
        Bundle b = new Bundle();
        b.putLong("time_data", time_data);
        b.putInt("quantity_data", quantity_data);
        data.putExtras(b); //Put data to Intent
        setResult(RESULT, data);
        finish();
    }

    private void updateTimer(){
        int seconds = (int) (mtimeLeftinMilliseconds / 1000) % 60;

//        String timeLeftText = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        if (timeCounter != null) {
            timeCounter.setText(String.valueOf(seconds));
        }
        timeVar = (mtimeLeftinMilliseconds / 1000);
//
//        if (timeVar < 1) {
//            decreaseQuantity();
//            setBreakTime();
//        }
    }
}
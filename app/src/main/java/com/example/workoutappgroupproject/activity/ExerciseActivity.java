package com.example.workoutappgroupproject.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.fragment.ExerciseFragment;
import com.google.android.material.snackbar.Snackbar;

public class ExerciseActivity extends AppCompatActivity {
    String type;
    int size;
    int firstID;
    int maxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState != null) {
            return;
        }
        Intent intent = getIntent();
        if (intent.hasExtra("TYPE")){
            type = intent.getStringExtra("TYPE");
        }
        if (intent.hasExtra("SIZE")){
            size = intent.getIntExtra("SIZE",0);
        }
        if (intent.hasExtra("FIRST_ID")){
            firstID = intent.getIntExtra("FIRST_ID",3);
        }
        if (intent.hasExtra("MAX_TIME")){
            maxTime = intent.getIntExtra("MAX_TIME",0);
        }
        ConstraintLayout fl = (ConstraintLayout) findViewById(R.id.mainView);
        fl.removeAllViews();
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.mainView, new ExerciseFragment(type,size,0,firstID,maxTime));
        transaction1.commit();
    }
}
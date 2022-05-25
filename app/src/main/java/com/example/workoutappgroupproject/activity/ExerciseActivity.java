package com.example.workoutappgroupproject.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.fragment.ExerciseFragment;

public class ExerciseActivity extends AppCompatActivity {
    String type;
    int firstID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Intent intent = getIntent();
        if (intent.hasExtra("TYPE")){
            type = intent.getStringExtra("TYPE");
        }
        if (intent.hasExtra("FIRST_ID")){
            firstID = intent.getIntExtra("FIRST_ID",-1);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.mainView, new ExerciseFragment(type,firstID)).addToBackStack(null).commit();
//        FragmentTransaction fragmentTransaction;
//        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.nav_fragment, new ExerciseFragment());

    }
}
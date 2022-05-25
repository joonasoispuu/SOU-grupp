package com.example.workoutappgroupproject.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.workoutappgroupproject.ExampleDialog;
import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.ActivityMainBinding;
import com.example.workoutappgroupproject.fragment.ArmsandchestFragment;
import com.example.workoutappgroupproject.fragment.CustomExerciseFragment;
import com.example.workoutappgroupproject.fragment.ExerciseFragment;
import com.example.workoutappgroupproject.fragment.ProfileFragment;
import com.example.workoutappgroupproject.fragment.RunFragment;
import com.example.workoutappgroupproject.fragment.SixpackFragment;
import com.example.workoutappgroupproject.fragment.TrainFragment;
import com.example.workoutappgroupproject.UserDB.User;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private ExerciseViewModel exerciseViewModel;
    BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    ConstraintLayout mainView;
    int oldId;
    private Boolean isExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainView = findViewById(R.id.mainView);
        // init view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        userViewModel.getIsExists().observe(this, isExists -> { this.isExists = isExists; });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // set navigation bar position to profileFragment by default
        bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        //replaceFragment(new ProfileFragment(),1,false);

        // bottom nav logic
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // never select an already selected item from bottom navbar
            if (item.getItemId() == bottomNavigationView.getSelectedItemId()) return false;
            switch(item.getItemId()){
                case R.id.runFragment:
                    // check if user exists
                    if (!isExists) {
                        openDialog();
                        return false;
                    }
                    replaceFragment(new RunFragment(),-1,false);
                    break;
                case R.id.profileFragment:
                    if(oldId != R.id.runFragment) replaceFragment(new ProfileFragment(),-1,false);
                    else replaceFragment(new ProfileFragment(),1,false);
                    break;
                case R.id.trainFragment:
                    // check if user exists
                    if (!isExists) {
                        openDialog();
                        return false;
                    }
                    replaceFragment(new TrainFragment(),1,false);
                    break;
            }
            oldId = item.getItemId();
            return true;
        });

    }

    private void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(),"example dialog");
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //On TrainFragment user can choose what exercise do to by having the fragment replaced
    public void OnChosenExercise(View view){
        String Exercise = view.getTag().toString();
        if (!Exercise.equals("AddCustom")) {
            Intent activity1 = new Intent(MainActivity.this, ExerciseActivity.class);
            activity1.putExtra("TYPE", Exercise);
            exerciseViewModel.getAllExercisesByType("ArmsandChest").observe(this,exercises -> {
                int size = exercises.size();
                if (size>=1) System.out.println("NAME: "+exercises.get(1).getName());
                else System.out.println("NAME: "+exercises.get(1).getName());

            });
            // check if id matches
//            activity1.putExtra("TYPE", 2);
//            startActivity(activity1);
        } else {
            Intent customActivity  = new Intent(MainActivity.this, CustomActivity.class);
            startActivity(customActivity);
        }
    }
}
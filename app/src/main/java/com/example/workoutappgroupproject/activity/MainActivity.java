package com.example.workoutappgroupproject.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.workoutappgroupproject.CustomDialog;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.ActivityMainBinding;
import com.example.workoutappgroupproject.fragment.ProfileFragment;
import com.example.workoutappgroupproject.fragment.RunFragment;
import com.example.workoutappgroupproject.fragment.TrainFragment;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
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
        CustomDialog customDialog = new CustomDialog();
        customDialog.show(getSupportFragmentManager(),"example dialog");
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
        switch (Exercise){
            case "Sixpack":
//                replaceFragment(new ExerciseFragment(),2,true);
                Intent activity1  = new Intent(MainActivity.this, ExerciseActivity.class);
                activity1.putExtra("TYPE","Sixpack");
                startActivity(activity1);
                break;
            case "ArmsandChest":
//                replaceFragment(new ArmsandchestFragment(),2,true);
                Intent activity2  = new Intent(MainActivity.this, ExerciseActivity.class);
                activity2.putExtra("TYPE","ArmsandChest");
                startActivity(activity2);
                break;
            case "Custom":
                Intent activity3  = new Intent(MainActivity.this, ExerciseActivity.class);
                activity3.putExtra("TYPE","Custom");
                startActivity(activity3);
                break;
            case "AddCustom":
                Intent customActivity  = new Intent(MainActivity.this, CustomActivity.class);
                startActivity(customActivity);
                break;
        }
    }
}
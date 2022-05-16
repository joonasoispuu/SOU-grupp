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
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.ActivityMainBinding;
import com.example.workoutappgroupproject.fragment.ArmsandchestFragment;
import com.example.workoutappgroupproject.fragment.ProfileFragment;
import com.example.workoutappgroupproject.fragment.RunFragment;
import com.example.workoutappgroupproject.fragment.SixpackFragment;
import com.example.workoutappgroupproject.fragment.TrainFragment;
import com.example.workoutappgroupproject.room.User;
import com.example.workoutappgroupproject.room.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    ConstraintLayout mainView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;
    //    Button btnSaveProfile;
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

        // check if app is on it's first run
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();
        if (isItFirstTime()) {
            System.out.println("First time");
            // TODO: prompt user to enter data
        } else {
            System.out.println("Not First Time");
            // get users data
//            userViewModel.getAllUsers().observe(this, this::getUserData);
        }



        // TODO: ask user for name, height, weight
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

    private boolean isItFirstTime() {
        if (sharedPreferences.getBoolean("firstTime", true)) {
            sharedEditor.putBoolean("firstTime", false);
            sharedEditor.commit();
            sharedEditor.apply();
            return true;
        } else {
            return false;
        }
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir >= 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //On TrainFragment user can choose what exercise do to by having the fragment replaced
    public void OnChosenExercise(View view){
        String Exercise = view.getTag().toString();
        switch (Exercise){
            case "ArmsandChest":
                replaceFragment(new ArmsandchestFragment(),1,true);
                break;
            case "Sixpack":
                break;
            case "Custom":
                Intent intent  = new Intent(MainActivity.this, CustomActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean hasUserData(List<User> users) {
        int pos = 0;
        // iterate users list
        while (pos < users.size()){
            String name = users.get(pos).getName();
            float height = users.get(pos).getHeight();
            float weight = users.get(pos).getWeight();
            int age = users.get(pos).getAge();
            System.out.println("---USER---"+"\n"+ "id: "+pos+"\n"+ "name: "+name +
                    "\n"+"height: "+height+"\n"+ "weight: "+weight+ "\n"+ "age: "+age+"\n");
            pos++;
        }
        return pos > 0;
    }

    private LiveData<Boolean> isExists() {
        return userViewModel.getIsExists();
    }
}
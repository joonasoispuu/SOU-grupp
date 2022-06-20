package com.example.workoutappgroupproject.activity;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.workoutappgroupproject.CustomDialog;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.ActivityMainBinding;
import com.example.workoutappgroupproject.fragment.ProfileFragment;
import com.example.workoutappgroupproject.fragment.SettingsFragment;
import com.example.workoutappgroupproject.fragment.TrainFragment;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    private void setActionBarColor(int color) {
        if (actionBar != null) {
            ColorDrawable colorDrawable
                    = new ColorDrawable(color);
            assert actionBar != null;
            actionBar.setBackgroundDrawable(colorDrawable);
        }
    }

    BottomNavigationView bottomNavigationView;

    ActivityMainBinding binding;
    ConstraintLayout mainView;
    int oldId = 0;
    private Boolean user_isExists = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("oldId",oldId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        oldId = savedInstanceState.getInt("oldId");
        System.out.println("SAVED oldId: "+oldId);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateTheme();

        actionBar = getSupportActionBar();
//        int color = getResources().getColor(R.color.purple_500);
//        setActionBarColor(color);

        mainView = findViewById(R.id.mainView);
        // init view model
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getIsExists().observe(this, isExists -> { this.user_isExists = isExists; });

        // set BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // set navigation bar position to profileFragment by default
        bottomNavigationView.setSelectedItemId(R.id.profileFragment);

        // bottom nav logic
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // never select an already selected item from bottom navbar
            if (item.getItemId() == bottomNavigationView.getSelectedItemId()) return false;
            switch(item.getItemId()){
                case R.id.settingsFragment:
                    replaceFragment(new SettingsFragment(),-1,false);
                    break;
                case R.id.profileFragment:
                    if(oldId != R.id.settingsFragment) replaceFragment(new ProfileFragment(),-1,false);
                    else replaceFragment(new ProfileFragment(),1,false);
                    break;
                case R.id.trainFragment:
                    // check if user exists
                    if (!user_isExists) {
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

    public void updateTheme() {
        boolean theme;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        theme = sharedPreferences.getBoolean(getString(R.string.pref_key_theme),false);
        if (theme) {
            if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES) return;
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else {
            if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_FOLLOW_SYSTEM) return;
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void openDialog() {
        bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        CustomDialog exampleDialog = new CustomDialog();
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

}
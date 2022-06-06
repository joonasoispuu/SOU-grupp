package com.example.workoutappgroupproject.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutappgroupproject.CustomDialog;
import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.SettingsFragment;
import com.example.workoutappgroupproject.databinding.ActivityMainBinding;
import com.example.workoutappgroupproject.fragment.ProfileFragment;
import com.example.workoutappgroupproject.fragment.RunFragment;
import com.example.workoutappgroupproject.fragment.TrainFragment;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    ConstraintLayout mainView;
    int oldId;
    private Boolean user_isExists = false;
    private Boolean exercise_isExists = false;
    private List<Exercise> exercisesSixpack;
    private List<Exercise> exercisesArmsandChest;
    private List<Exercise> exercisesCustom;

    private static final int RESULT_NOT_SUCCESS = 200;
    public static final int RESULT_SUCCESS = 100;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_SUCCESS){
                    Intent resultData = result.getData();
                    if (resultData != null) {
                        Snackbar.make(findViewById(R.id.myCoordinatorMain), "Exercise Session successful!",
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(R.id.myCoordinatorMain), "Did not get data!", Snackbar.LENGTH_SHORT).show();
                    }

                }else if(result.getResultCode() == RESULT_NOT_SUCCESS){
                    Intent resultData = result.getData();
                    if (resultData != null) {
                        Snackbar.make(findViewById(R.id.myCoordinatorMain), "Exercise Session failed!",
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(R.id.myCoordinatorMain), "Did not get data!", Snackbar.LENGTH_SHORT).show();
                    }

                }else{
                    Snackbar.make(findViewById(R.id.myCoordinatorMain), "Exercise Session canceled!",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
    );

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainView = findViewById(R.id.mainView);
        // init view model
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getIsExists().observe(this, isExists -> { this.user_isExists = isExists; });
        ExerciseViewModel exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        exerciseViewModel.getIsExists().observe(this, isExists -> { this.exercise_isExists = isExists; });
        // get Exercise List by Type -> save each list into List<Exercise> variable
        exerciseViewModel.getAllExercisesByType(getString(R.string.sixpack)).observe(this, exercisesSixpack -> {
            this.exercisesSixpack = exercisesSixpack;
        });
        exerciseViewModel.getAllExercisesByType(getString(R.string.armsandchest)).observe(this, exercisesArmsandChest -> {
            this.exercisesArmsandChest = exercisesArmsandChest;
        });
        exerciseViewModel.getAllExercisesByType(getString(R.string.custom)).observe(this, exercisesCustom -> {
            this.exercisesCustom = exercisesCustom;
        });

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
                    // check if user exists
                    if (!user_isExists) {
                        openDialog();
                        return false;
                    }
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

    private void openDialog() {
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

    //On TrainFragment user can choose what exercise do to by having the fragment replaced
    public void OnChosenExercise(View view){
        String Exercise = view.getTag().toString();
        switch (Exercise){
            case "Sixpack":
                Intent intent = new Intent(MainActivity.this, ExerciseActivity.class);
                intent.putExtra("TYPE",getString(R.string.sixpack));
                intent.putExtra("SIZE",exercisesSixpack.size());
                activityResultLauncher.launch(intent);
                break;
            case "ArmsandChest":
                Intent intent2 = new Intent(MainActivity.this, ExerciseActivity.class);
                intent2.putExtra("TYPE",getString(R.string.armsandchest));
                intent2.putExtra("SIZE",exercisesArmsandChest.size());
                activityResultLauncher.launch(intent2);
                break;
            case "Custom":
                Intent intent3 = new Intent(MainActivity.this, ExerciseActivity.class);
                intent3.putExtra("TYPE",getString(R.string.custom));
                intent3.putExtra("SIZE",exercisesCustom.size());
                activityResultLauncher.launch(intent3);
                break;
            case "AddCustom":
                Intent customActivity  = new Intent(MainActivity.this, CustomActivity.class);
                startActivity(customActivity);
                break;
        }
    }
}
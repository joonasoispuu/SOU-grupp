package com.example.workoutappgroupproject.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.ActivityMainBinding;
import com.example.workoutappgroupproject.fragment.ArmsandchestFragment;
import com.example.workoutappgroupproject.fragment.CustomFragment;
import com.example.workoutappgroupproject.fragment.ProfileFragment;
import com.example.workoutappgroupproject.fragment.RunFragment;
import com.example.workoutappgroupproject.fragment.SixpackFragment;
import com.example.workoutappgroupproject.fragment.TrainFragment;
import com.example.workoutappgroupproject.room.User;
import com.example.workoutappgroupproject.room.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    ConstraintLayout mainView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainView = findViewById(R.id.mainView);
        // init view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // check if app is on it's first run
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();
        if (isItFirestTime()) {
            System.out.println("First time");
            // TODO: prompt user to enter data
        } else {
            System.out.println("Not First Time");
            // get users data
            userViewModel.getAllUsers().observe(this, this::getUserData);
        }

        /*
        userViewModel.getIsExists().observe(this, isExists ->{
            if (!isExists) {
                // no users in db
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Welcome!");
                builder.setMessage("What's your name?");
                builder.setCancelable(false);
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
                final EditText input = (EditText) view.findViewById(R.id.etName);
                builder.setView(view);
                // builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.setPositiveButton("Save", (dialog, which) -> {});
                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String first = input.getText().toString().trim();
                        String last = input.getText().toString().trim();
                        float height = 0;
                        float weight = 0;
                        int age = 19;
                        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last)) {
                            input.setError("Field can't be empty!");
                            return;
                        }
                        // save data to db ...
                        User user = new User(first,last,height,weight,age);
                        userViewModel.insert(user);
                        Toast.makeText(MainActivity.this, "Name saved to db " + input.getText().toString(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
            } else {
                // users exist in db
                userViewModel.getAllUsers().observe(this,users -> {
                    int pos = 0;
                    while (pos < users.size()){
                        getUserData(users,pos);
                        pos++;
                    }
                });
            }
        });
         */
        // TODO: ask user for name, height, weight
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // set navigation bar position to profileFragment by default
        bottomNavigationView.setSelectedItemId(R.id.profileFragment);
        replaceFragment(new ProfileFragment());
        // bottom nav logic
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // never select an already selected item from bottom navbar
            if(item.getItemId() == bottomNavigationView.getSelectedItemId()) return false;
            switch(item.getItemId()){
                case R.id.runFragment:
                    replaceFragment(new RunFragment());
                    break;
                case R.id.profileFragment:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.trainFragment:
                    replaceFragment(new TrainFragment());
                    break;
            }
            return true;
        });

    }

    private boolean isItFirestTime() {
        if (sharedPreferences.getBoolean("firstTime", true)) {
            sharedEditor.putBoolean("firstTime", false);
            sharedEditor.commit();
            sharedEditor.apply();
            return true;
        } else {
            return false;
        }
    }

    private void getUserData(List<User> users) {
        int pos = 0;
        // iterate users list
        while (pos < users.size()){
            String first = users.get(pos).getFirstName();
            String last = users.get(pos).getLastName();
            float height = users.get(pos).getHeight();
            float weight = users.get(pos).getWeight();
            int age = users.get(pos).getAge();
            System.out.println("---USER---"+"\n"+ "id: "+pos+"\n"+ "first: "+first+
                    "\n"+ "last: "+last+"\n"+ "height: "+height+"\n"+
                    "weight: "+weight+ "\n"+ "age: "+age+"\n");
            pos++;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //On TrainFragment user can choose what exercise do to by having the fragment replaced
    public void OnChosenExercise(View view){
        String Exercise = view.getTag().toString();
        switch (Exercise){
            case "ArmsandChest":
                replaceFragment(new ArmsandchestFragment());
                break;
            case "Sixpack":
                replaceFragment(new SixpackFragment());
                break;
            case "Custom":
                replaceFragment(new CustomFragment());
                break;
        }
    }
}
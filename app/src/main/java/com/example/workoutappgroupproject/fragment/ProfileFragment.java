package com.example.workoutappgroupproject.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.UserDB.User;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;

    public static final int RESULT_SAVE = 100;
    public static final int RESULT_EDIT = 200;
    private static final ArrayList<User> userArrayList = new ArrayList<>();
    private String dataStatus = "Empty";
    private float BMI;

    TextInputLayout textInputName, textInputHeight, textInputWeight, textInputAge;
    Button btnSaveProfile;
    TextView txtBMI, txtDescription, txtBMIWarning;

    public ProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("state",dataStatus);
        System.out.println("onSaveInstanceState dataStatus: "+dataStatus);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textInputName = view.findViewById(R.id.textInputName);
        textInputHeight = view.findViewById(R.id.textInputHeight);
        textInputWeight = view.findViewById(R.id.textInputWeight);
        textInputAge = view.findViewById(R.id.textInputAge);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        txtBMI=view.findViewById(R.id.BMI);
        txtDescription=view.findViewById(R.id.BMI_Description);
        txtBMIWarning=view.findViewById(R.id.BMI_Warning);

        // init view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // save dataStatus to savedInstanceState key value set
        if (savedInstanceState != null) {
            dataStatus = savedInstanceState.getString("state","Empty");
        }

        // check if data already there
        userViewModel.getIsExists().observe(getViewLifecycleOwner(), isExists -> {
            if (!isExists) {
                // no users in db
                System.out.println("current dataStatus: "+dataStatus);
            } else {
                // users exist in db
                userViewModel.getAllUsers().observe(getViewLifecycleOwner(),users -> {
                    boolean gotUsers = hasUserData(users);
                    if (gotUsers) {
                        if (!dataStatus.equals("Editing")) {
                            int id = 0; // <-- first User object in list
                            String name = users.get(id).getName();
                            textInputName.getEditText().setText(name);
                            float height = users.get(id).getHeight();
                            textInputHeight.getEditText().setText(String.valueOf(height));
                            float weight = users.get(id).getWeight();
                            textInputWeight.getEditText().setText(String.valueOf(weight));
                            int age = users.get(id).getAge();
                            textInputAge.getEditText().setText(String.valueOf(age));
                            // set textInputs
                            textInputName.setCounterEnabled(false);
                            textInputName.setEnabled(false);
                            textInputHeight.setEnabled(false);
                            textInputWeight.setEnabled(false);
                            textInputAge.setEnabled(false);
                            btnSaveProfile.setText(R.string.btn_edit);
                            dataStatus = "Saved";
                            float bmiheight = height/100;
                            BMI=weight/(bmiheight*bmiheight);
                            txtBMI.setText((String.format("BMI: "+BMI)));
                            if(age<18){
                                txtBMIWarning.setText(R.string.bmi_warning);
                            }
                            else{
                                txtBMIWarning.setText("");
                            }
                            if(BMI<16){
                                txtDescription.setText(getString(R.string.moderately_skinny));
                            }
                            else if(BMI<16.9 && BMI >16){
                                txtDescription.setText(getString(R.string.moderately_skinny));
                            }
                            else if(BMI<18.4 && BMI >17){
                                txtDescription.setText(getString(R.string.mildly_skinny));
                            }
                            else if(BMI<24.9 && BMI >18.5){
                                txtDescription.setText(getString(R.string.normal));
                            }
                            else if(BMI<29.9 && BMI >25){
                                txtDescription.setText(getString(R.string.overweight));
                            }
                            else if(BMI<34.9 && BMI >30){
                                txtDescription.setText(getString(R.string.obese_class_I));
                            }
                            else{
                                txtDescription.setText(getString(R.string.obese_class_II));
                            }
                        }
                        System.out.println("current dataStatus: "+dataStatus);
                    }
                });
            }
            // when current status == Editing
            if(dataStatus.equals("Editing")){
                textInputName.setCounterEnabled(true);
                textInputName.setEnabled(true);
                textInputHeight.setEnabled(true);
                textInputWeight.setEnabled(true);
                textInputAge.setEnabled(true);
                btnSaveProfile.setText(R.string.btn_update);
            }
        });
        // save button click listener
        btnSaveProfile.setOnClickListener(view1 -> {
            switch (dataStatus) {
                case "Empty": {
                    if (!validateName(view) | !validateHeight(view) | !validateWeight(view) | !validateAge(view)){
                        // cancel
                        return;
                    }
                    String name = textInputName.getEditText().getText().toString().trim();
                    float height = Float.parseFloat(textInputHeight.getEditText().getText().toString().trim());
                    float weight = Float.parseFloat(textInputWeight.getEditText().getText().toString().trim());
                    int age = 0;
                    try {
                        age = Integer.parseInt(textInputAge.getEditText().getText().toString().trim());
                    } catch (NumberFormatException nfe) {
                        textInputAge.getEditText().setError("ERROR");
                        System.out.println("NumberFormat Exception: invalid input string");
                    }
                    // save data to db ...
                    User user = new User(name,height,weight,age);
                    userViewModel.insert(user);
                    Toast.makeText(getActivity(), "User created successfully! ", Toast.LENGTH_LONG)
                            .show();
                    break;
                }
                case "Saved": {
                    textInputName.setCounterEnabled(true);
                    textInputName.setEnabled(true);
                    textInputHeight.setEnabled(true);
                    textInputWeight.setEnabled(true);
                    textInputAge.setEnabled(true);
                    btnSaveProfile.setText(R.string.btn_update);
                    dataStatus = "Editing";
                    System.out.println("current dataStatus: "+dataStatus);
                    break;
                }
                case "Editing": {
                    if (!validateName(view) | !validateHeight(view) | !validateWeight(view) | !validateAge(view)){
                        // cancel
                        return;
                    } else {
                        if (!validateUpdate(0)) return;
                    }
                    String name = textInputName.getEditText().getText().toString().trim();
                    float height = Float.parseFloat(textInputHeight.getEditText().getText().toString().trim());
                    float weight = Float.parseFloat(textInputWeight.getEditText().getText().toString().trim());
                    int age = Integer.parseInt(textInputAge.getEditText().getText().toString().trim());
                    // save updated data to db ...
                    User user = new User(name,height,weight,age);
                    user.setId(1);
                    userViewModel.update(user);
                    Toast.makeText(getActivity(), "User updated successfully! ", Toast.LENGTH_LONG)
                            .show();
                    dataStatus = "Saved";
                    break;
                }
            }
        });
    }

    private boolean validateUpdate(int id) {
        List<User> users = userViewModel.getAllUsers().getValue();

        String name = users.get(id).getName();
        String oldName = textInputName.getEditText().getText().toString();

        float height = users.get(id).getHeight();
        float oldHeight = Float.parseFloat(textInputHeight.getEditText().getText().toString());

        float weight = users.get(id).getWeight();
        float oldWeight = Float.parseFloat(textInputWeight.getEditText().getText().toString());

        int age = users.get(id).getAge();
        int oldAge = Integer.parseInt(textInputAge.getEditText().getText().toString());

        // if no field is different from current User data
        if (name.equals(oldName) && height == oldHeight && weight == oldWeight && age == oldAge) {
            Snackbar.make(getActivity().findViewById(R.id.mainView),
                    "Can't save already existing data. Make sure at least 1 field is unique", 5000).show();
            return false;
        }
        return true;
    }

    private boolean validateWeight(View v) {
        String weightInput = textInputWeight.getEditText().getText().toString().trim();

        if (weightInput.isEmpty()) {
            textInputWeight.setError("Field cannot be empty!");
            return false;
        } else {
            float weight = Float.parseFloat(weightInput);
            if (weight > 635) {
                textInputWeight.setError("Too fat! Max: 635kg");
                return false;
            }
            textInputWeight.setError(null);
            return true;
        }
    }

    private boolean validateHeight(View v) {
        String heightInput = textInputHeight.getEditText().getText().toString().trim();

        if (heightInput.isEmpty()) {
            textInputHeight.setError("Field cannot be empty!");
            return false;
        } else {
            float height = Float.parseFloat(heightInput);
            if (height > 272) {
                textInputHeight.setError("Too tall! Max: 272cm");
                return false;
            }
            textInputHeight.setError(null);
            return true;
        }
    }

    private boolean validateName(View v) {
        String nameInput = textInputName.getEditText().getText().toString().trim();
        if (nameInput.isEmpty()) {
            textInputName.setError("Field cannot be empty!");
            return false;
        } else if (nameInput.length() > 20) {
            textInputName.setError("Field cannot contain more than 20 characters!");
            return false;
        } else if (!isFullname(nameInput)) {
            textInputName.setError("Name not formatted correctly!");
            return false;
        } else {
            textInputName.setError(null);
            return true;
        }
    }

    private boolean isFullname(String str) {
        String expression = "^[-.a-zA-Z\\s]+";
        return str.matches(expression);
    }

    private boolean validateAge(View v) {
        String ageInput = textInputAge.getEditText().getText().toString().trim();
        int age = Integer.parseInt(ageInput);
        if (ageInput.isEmpty()) {
            textInputAge.setError("Field cannot be empty!");
            return false;
        } else if (ageInput.length() > 3) {
            textInputAge.setError("Too large number!");
            return false;
        } else if (age < 12) {
            textInputAge.setError("Age cannot be "+age+"! Min: 12!");
            return false;
        } else {
            textInputAge.setError(null);
            return true;
        }
    }

    private boolean hasUserData(List<User> users) {
        // iterate users list
        int id = -1;
        for(int i = 0; i<users.size(); i++){
            id = users.get(i).getId();
            String name = users.get(i).getName();
            float height = users.get(i).getHeight();
            float weight = users.get(i).getWeight();
            int age = users.get(i).getAge();
            System.out.println("---USER---"+"\n"+ "id: "+id+"\n"+ "name: "+name +
                    "\n"+"height: "+height+"\n"+ "weight: "+weight+ "\n"+ "age: "+age+"\n");
        }
        return (id != -1);
    }
}
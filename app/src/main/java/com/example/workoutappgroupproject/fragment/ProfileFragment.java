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
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.room.User;
import com.example.workoutappgroupproject.room.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;

    public static final int RESULT_SAVE = 100;
    public static final int RESULT_EDIT = 200;
    private String dataStatus = "Empty";

    TextInputLayout textInputName, textInputHeight, textInputWeight, textInputAge;
    Button btnSaveProfile, btnRemoveProfile;

    public ProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("state",dataStatus);
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
        btnRemoveProfile = view.findViewById(R.id.btnRemoveProfile);
        TextView title = view.findViewById(R.id.Profile);
        title.setText(dataStatus);

        // init view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // check if data already there
        userViewModel.getIsExists().observe(getViewLifecycleOwner(), isExists ->{
            if (!isExists) {
//                // no users in db
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Welcome!");
//                builder.setMessage("What's your name?");
//                builder.setCancelable(false);
//                View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
//                final EditText input = (EditText) view.findViewById(R.id.etName);
//                builder.setView(view);
//                // builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//                builder.setPositiveButton("Save", (dialog, which) -> {});
//                AlertDialog alert = builder.create();
//                alert.show();
//                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String first = input.getText().toString().trim();
//                        String last = input.getText().toString().trim();
//                        float height = 0;
//                        float weight = 0;
//                        int age = 19;
//                        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last)) {
//                            input.setError("Field can't be empty!");
//                            return;
//                        }
//                        // save data to db ...
//                        User user = new User(first,last,height,weight,age);
//                        userViewModel.insert(user);
//                        Toast.makeText(getActivity(), "Name saved to db " + input.getText().toString(), Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
            } else {
                // users exist in db
                userViewModel.getAllUsers().observe(getViewLifecycleOwner(),users -> {
                    boolean gotUsers = hasUserData(users);
                    if (gotUsers) {
                        int pos = 0;
                        // iterate users list
                        while (pos < users.size()){
                            String name = users.get(pos).getName();
                            float height = users.get(pos).getHeight();
                            float weight = users.get(pos).getWeight();
                            int age = users.get(pos).getAge();
                            pos++;
                            textInputName.getEditText().setText(name);
                            textInputHeight.getEditText().setText(String.valueOf(height));
                            textInputWeight.getEditText().setText(String.valueOf(weight));
                            textInputAge.getEditText().setText(String.valueOf(age));
                        }
                        textInputName.setCounterEnabled(false);
                        textInputName.setEnabled(false);
                        textInputHeight.setEnabled(false);
                        textInputWeight.setEnabled(false);
                        textInputAge.setEnabled(false);
                        btnSaveProfile.setText(R.string.btn_edit);
                        if (savedInstanceState == null) {
                            dataStatus = "Saved";
                        }
                        else if (dataStatus.equals("Empty")) {
                            dataStatus = savedInstanceState.getString("state","Empty");
                        }
                    }
                    // reload user form when data loads (on screen rotation)
                    if(dataStatus.equals("Editing")){
                        textInputName.setCounterEnabled(true);
                        textInputName.setEnabled(true);
                        textInputHeight.setEnabled(true);
                        textInputWeight.setEnabled(true);
                        textInputAge.setEnabled(false);
                        btnSaveProfile.setText(R.string.btn_update);
                        btnRemoveProfile.setEnabled(false);
                    }
                    title.setText(dataStatus);
                });
            }
        });
        // save button click listener
        btnSaveProfile.setOnClickListener(view1 -> {
            if (dataStatus.equals("Empty")){
                if (!validateName(view) | !validateHeight(view) | !validateWeight(view) | !validateAge(view)){
                    // cancel
                    return;
                }
                String name = textInputName.getEditText().getText().toString();
                float height = Float.parseFloat(textInputHeight.getEditText().getText().toString());
                float weight = Float.parseFloat(textInputWeight.getEditText().getText().toString());
                int age = Integer.parseInt(textInputAge.getEditText().getText().toString());
                // save data to db ...
                User user = new User(name,height,weight,age);
                userViewModel.insert(user);
                //Toast.makeText(getActivity(), "Data saved: " + name, Toast.LENGTH_LONG)
                //        .show();
                dataStatus = "Saved";
            } else if (dataStatus.equals("Saved")) {
                textInputName.setCounterEnabled(true);
                textInputName.setEnabled(true);
                textInputHeight.setEnabled(true);
                textInputWeight.setEnabled(true);
                textInputAge.setEnabled(false);
                btnSaveProfile.setText(R.string.btn_update);
                btnRemoveProfile.setEnabled(false);
                dataStatus = "Editing";
            } else if (dataStatus.equals("Editing")) {
                if (!validateName(view) | !validateHeight(view) | !validateWeight(view) | !validateAge(view)){
                    // cancel
                    return;
                }
                String name = textInputName.getEditText().getText().toString();
                float height = Float.parseFloat(textInputHeight.getEditText().getText().toString());
                float weight = Float.parseFloat(textInputWeight.getEditText().getText().toString());
                int age = Integer.parseInt(textInputAge.getEditText().getText().toString());
                // save updated data to db ...
                User user = new User(name,height,weight,age);
                user.setId(1);
                userViewModel.update(user);
                //Toast.makeText(getActivity(), "Data updated: " + name, Toast.LENGTH_LONG)
                //        .show();
                btnRemoveProfile.setEnabled(true);
                dataStatus = "Saved";
            } else {
            }
            title.setText(dataStatus);
        });
    }

    private boolean validateWeight(View v) {
        String weightInput = textInputWeight.getEditText().getText().toString().trim();
        if (weightInput.isEmpty()) {
            textInputWeight.setError("Field cannot be empty!");
            return false;
        } else {
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
        } else {
            textInputName.setError(null);
            return true;
        }
    }

    private boolean validateAge(View v) {
        String ageInput = textInputAge.getEditText().getText().toString().trim();
        if (ageInput.isEmpty()) {
            textInputAge.setError("Field cannot be empty!");
            return false;
        } else {
            textInputAge.setError(null);
            return true;
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
}
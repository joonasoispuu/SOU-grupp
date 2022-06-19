package com.example.workoutappgroupproject.fragment;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.UserDB.User;
import com.example.workoutappgroupproject.databinding.FragmentProfileBinding;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    ConstraintLayout mainView;
    ActionBar actionBar;
    ProgressBar progressBar;
    private UserViewModel userViewModel;
    private String profileStatus = "Empty";
    private float BMI;

    TextInputLayout textInputName, textInputHeight, textInputWeight, textInputAge;
//    Button btnSaveProfile;
    TextView txtBMI, txtDescription, txtBMIWarning;

    private static final int USER_ID = 0;
    public static final int RESULT_SAVE = 100;
    public static final int RESULT_EDIT = 200;

    private static final int RESULT_NOT_SUCCESS = 200;
    public static final int RESULT_SUCCESS = 100;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("state", profileStatus);
        System.out.println("onSaveInstanceState profileStatus: "+ profileStatus);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        switch (result) {
//            case 100:
//                System.out.println("RESULT_SUCCESS:"+result);
//                break;
//            case 200:
//                System.out.println("RESULT_NOT_SUCCESS:"+result);
//                break;
//            default:
//                System.out.println("RESULT_NULL");
//                break;
//        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //reset the menu at top
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.profile);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowHomeEnabled(true);
        }

        mainView = binding.mainView;
        onFragmentResult();

        textInputName = binding.textInputName;
        textInputHeight = binding.textInputHeight;
        textInputWeight = binding.textInputWeight;
        textInputAge = binding.textInputAge;
        txtBMI = binding.BMI;
        txtDescription = binding.BMIDescription;
        txtBMIWarning = binding.BMIWarning;
        progressBar = binding.progressBar;

        textInputName.setVisibility(View.INVISIBLE);
        textInputHeight.setVisibility(View.INVISIBLE);
        textInputWeight.setVisibility(View.INVISIBLE);
        textInputAge.setVisibility(View.INVISIBLE);
        txtBMIWarning.setVisibility(View.GONE);
        txtBMI.setVisibility(View.GONE);
        txtDescription.setVisibility(View.GONE);

//        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.getMenu().getItem(0).setEnabled(true);
//        bottomNavigationView.getMenu().getItem(1).setEnabled(true);
//        bottomNavigationView.getMenu().getItem(2).setEnabled(true);

        // init view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // save profileStatus to savedInstanceState key value set
        if (savedInstanceState != null) {
            profileStatus = savedInstanceState.getString("state","Empty");
        }

        // check if data already there
        userViewModel.getIsExists().observe(getViewLifecycleOwner(), isExists -> {
            if (!isExists) {
                // no users in db
                progressBar.setVisibility(View.GONE);
                textInputName.setVisibility(View.VISIBLE);
                textInputHeight.setVisibility(View.VISIBLE);
                textInputWeight.setVisibility(View.VISIBLE);
                textInputAge.setVisibility(View.VISIBLE);
            } else {
                // users exist in db
                userViewModel.getAllUsers().observe(getViewLifecycleOwner(),users -> {
                    boolean gotUsers = hasUserData(users);
                    if (gotUsers) {
                        // TODO: check current BMI (from SharedPreferences)

                        if (!profileStatus.equals("Editing")) {
                            txtBMIWarning.setVisibility(View.VISIBLE);
                            txtBMI.setVisibility(View.VISIBLE);
                            txtDescription.setVisibility(View.VISIBLE);

                            int id = USER_ID; // <-- first User object in list
                            String name = users.get(id).getName();
                            textInputName.getEditText().setText(name);
                            float height = users.get(id).getHeight();
                            textInputHeight.getEditText().setText(String.valueOf(height));
                            float weight = users.get(id).getWeight();
                            textInputWeight.getEditText().setText(String.valueOf(weight));
                            int age = users.get(id).getAge();
                            textInputAge.getEditText().setText(String.valueOf(age));

                            progressBar.setVisibility(View.GONE);
                            textInputName.setVisibility(View.VISIBLE);
                            textInputHeight.setVisibility(View.VISIBLE);
                            textInputWeight.setVisibility(View.VISIBLE);
                            textInputAge.setVisibility(View.VISIBLE);

                            // refresh inputs
                            setup_TextInput(false);
                            profileStatus = "Saved";
                            float bmiheight = height/100;
                            BMI=weight/(bmiheight*bmiheight);
                            String bmiString = String.format(Locale.getDefault(),getString(R.string.BMI_format), BMI);
                            txtBMI.setText(bmiString);
                            if(age<18){
                                txtBMIWarning.setText(R.string.bmi_warning);
                            }
                            else{
                                txtBMIWarning.setText("");
                            }
                            if(BMI<16){
                                txtDescription.setText(getString(R.string.severely_skinny));
                            }
                            else if(BMI<17){
                                txtDescription.setText(getString(R.string.moderately_skinny));
                            }
                            else if(BMI<18.5 && BMI >=17){
                                txtDescription.setText(getString(R.string.mildly_skinny));
                            }
                            else if(BMI<25 && BMI >=18.5){
                                txtDescription.setText(getString(R.string.normal));
                            }
                            else if(BMI<30 && BMI >=25){
                                txtDescription.setText(getString(R.string.overweight));
                            }
                            else if(BMI<34.9 && BMI >=30){
                                txtDescription.setText(getString(R.string.obese_class_I));
                            }
                            else{
                                txtDescription.setText(getString(R.string.obese_class_II));
                            }
                        }
                    }
                });
            }
            System.out.println("current profileStatus: "+ profileStatus);
            // when editing profile
            if(profileStatus.equals("Editing")){
                // refresh inputs
                setup_TextInput(true);
            }
        });
    }

    // get fragment result from arguments
    private void onFragmentResult() {
        int result;
        Bundle bundle = getArguments();
        if (bundle != null) {
            Snackbar snackbar = null;
            if (bundle.containsKey("session_result")){
                result = getArguments().getInt("session_result");
                if(result == RESULT_SUCCESS){
                    snackbar = Snackbar.make(mainView, getString(R.string.ex_session_success),
                            Snackbar.LENGTH_SHORT);
                }else if(result == RESULT_NOT_SUCCESS){
                    snackbar = Snackbar.make(mainView, getString(R.string.ex_session_fail),
                            Snackbar.LENGTH_SHORT);
                }else{
                    snackbar = Snackbar.make(mainView, getString(R.string.ex_session_cancel),
                            Snackbar.LENGTH_SHORT);
                }
                getArguments().clear();
            }
            if (snackbar != null) {
                snackbar.setAnchorView(requireActivity().findViewById(R.id.bottomNavigationView));
                snackbar.show();
            }
        }
    }

    // refresh inputs
    private void setup_TextInput(boolean enabled) {
        textInputName.setCounterEnabled(enabled);
        textInputName.setEnabled(enabled);
        textInputHeight.setEnabled(enabled);
        textInputWeight.setEnabled(enabled);
        textInputAge.setEnabled(enabled);
    }

    // helper method for setting up listener for edit text
    private void listenForInput(TextInputLayout textInput) {
        textInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput.setError(null);
                validateAll(textInput);
            }

            private void validateAll(TextInputLayout textInput) {
                if (textInput.equals(textInputName)) {
                    validateName();
                } else if (textInput.equals(textInputHeight)) {
                    validateHeight();
                } else if (textInput.equals(textInputWeight)) {
                    validateWeight();
                } else {
                    validateAge();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // validate db<-->local fields
    private boolean validateUpdate(int id) {
        List<User> users = userViewModel.getAllUsers().getValue();

        String name = null;
        float height = 0;
        float weight = 0;
        int age = 0;
        if (users != null) {
            name = users.get(id).getName();
            height = users.get(id).getHeight();
            weight = users.get(id).getWeight();
            age = users.get(id).getAge();
        }
        String oldName = textInputName.getEditText().getText().toString();
        float oldHeight = Float.parseFloat(textInputHeight.getEditText().getText().toString());
        float oldWeight = Float.parseFloat(textInputWeight.getEditText().getText().toString());
        int oldAge = Integer.parseInt(textInputAge.getEditText().getText().toString());

        // if no field is different from current User data
        if (name.equals(oldName) && height == oldHeight && weight == oldWeight && age == oldAge) {
            Snackbar.make(requireActivity().findViewById(R.id.mainView),
                    getString(R.string.err_data_same), 5000).show();
            return false;
        }
        return true;
    }

    // validate weight
    private boolean validateWeight() {
        String weightInput = textInputWeight.getEditText().getText().toString().trim();
        float weight = -1;
        try {
            weight = Float.parseFloat(weightInput);
        } catch (NumberFormatException nfe) {
            System.out.println(getString(R.string.err_nfe));
        }
        if (weightInput.isEmpty()) {
            textInputWeight.setError(getString(R.string.err_field_empty));
            return false;
        } else {
            if (weight < 2.13f) {
                textInputWeight.setError(getString(R.string.err_weight_min));
                return false;
            }
            if (weight > 635f) {
                textInputWeight.setError(getString(R.string.err_weight_max));
                return false;
            }
            textInputWeight.setError(null);
            return true;
        }
    }

    // validate height
    private boolean validateHeight() {
        String heightInput = textInputHeight.getEditText().getText().toString().trim();
        float height = -1;
        try {
            height = Float.parseFloat(heightInput);
        } catch (NumberFormatException nfe) {
            System.out.println(getString(R.string.err_nfe));
        }
        if (heightInput.isEmpty()) {
            textInputHeight.setError(getString(R.string.err_field_empty));
            return false;
        } else {
            if (height < 54.6f) {
                textInputHeight.setError(String.format(getString(R.string.err_height_min),height));
                return false;
            }
            if (height > 272f) {
                textInputHeight.setError(getString(R.string.err_height_max));
                return false;
            }
            textInputHeight.setError(null);
            return true;
        }
    }

    // validate name
    private boolean validateName() {
        String nameInput = textInputName.getEditText().getText().toString().trim();
        if (nameInput.isEmpty()) {
            textInputName.setError(getString(R.string.err_field_empty));
            return false;
        } else if (nameInput.length() > 20) {
            textInputName.setError(getString(R.string.err_name_max));
            return false;
        } else if (!isFullname(nameInput)) {
            textInputName.setError(getString(R.string.err_name_format));
            return false;
        } else {
            textInputName.setError(null);
            return true;
        }
    }

    // name format regex
    private boolean isFullname(String str) {
        String expression = "^[-.a-zA-Z\\s]+";
        return str.matches(expression);
    }

    // validate age
    private boolean validateAge() {
        String ageInput = textInputAge.getEditText().getText().toString().trim();
        int age = -1;
        try {
            age = Integer.parseInt(ageInput);;
        } catch (NumberFormatException nfe) {
            System.out.println(getString(R.string.err_nfe));
        }
        if (ageInput.isEmpty()) {
            textInputAge.setError(getString(R.string.err_field_empty));
            return false;
        } else {
            if (ageInput.length() > 3) {
                textInputAge.setError(getString(R.string.err_age_max));
                return false;
            }
            if (age < 12) {
                textInputAge.setError(String.format(getString(R.string.err_age_min),age));
                return false;
            }
            textInputAge.setError(null);
            return true;
        }
    }

    // db debug
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

    // add menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_submit,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        switch (profileStatus) {
//            case "Empty":
//                menu.getItem(0).setIcon(R.drawable.ic_done).setTitle(R.string.save);
//                break;
//            case "Saved":
//                menu.getItem(0).setIcon(R.drawable.ic_edit).setTitle(R.string.edit);
//                break;
//            case "Editing":
//                menu.getItem(0).setIcon(R.drawable.ic_done).setTitle(R.string.save);
//                break;
//            default:
//                menu.getItem(0).setIcon(R.drawable.ic_edit).setTitle(R.string.edit);
//                break;
//        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit){
            onSave();
            invalidateOptionsMenu(requireActivity());
        }
        return false;
    }

    // validate & save to db
    private void onSave() {
        listenForInput(textInputHeight);
        listenForInput(textInputWeight);
        listenForInput(textInputName);
        listenForInput(textInputAge);

        // save button click listener
        switch (profileStatus) {
            case "Empty": {
                if (!validateName() | !validateHeight() | !validateWeight() | !validateAge()){
                    // cancel
                    return;
                }
                String name = textInputName.getEditText().getText().toString().trim();
                float height = Float.parseFloat(textInputHeight.getEditText().getText().toString().trim());
                float weight = Float.parseFloat(textInputWeight.getEditText().getText().toString().trim());
                int age = Integer.parseInt(textInputAge.getEditText().getText().toString().trim());
                // save data to db
                User user = new User(name,height,weight,age);
                userViewModel.insert(user);
                Toast.makeText(getActivity(), getString(R.string.user_create_success), Toast.LENGTH_LONG)
                        .show();
                break;
            }
            case "Saved": {
                txtBMIWarning.setVisibility(View.GONE);
                txtBMI.setVisibility(View.GONE);
                txtDescription.setVisibility(View.GONE);
                textInputName.setCounterEnabled(true);
                textInputName.setEnabled(true);
                textInputHeight.setEnabled(true);
                textInputWeight.setEnabled(true);
                textInputAge.setEnabled(true);
                profileStatus = "Editing";
                System.out.println("current profileStatus: "+ profileStatus);
                break;
            }
            case "Editing": {
                if (!validateName() | !validateHeight() | !validateWeight() | !validateAge()){
                    // cancel
                    return;
                } else {
                    if (!validateUpdate(USER_ID)) return;
                }
                String name = textInputName.getEditText().getText().toString().trim();
                float height = Float.parseFloat(textInputHeight.getEditText().getText().toString().trim());
                float weight = Float.parseFloat(textInputWeight.getEditText().getText().toString().trim());
                int age = Integer.parseInt(textInputAge.getEditText().getText().toString().trim());
                // save updated data to db ...
                User user = new User(name,height,weight,age);
                user.setId(USER_ID+1);
                userViewModel.update(user);
                Toast.makeText(getActivity(), getString(R.string.user_update_success), Toast.LENGTH_LONG)
                        .show();
                profileStatus = "Saved";
                break;
            }
        }
//        MenuInflater menu = requireActivity().getMenuInflater(); menu.inflate(R.menu.item1, menu);
//        Menu menu =
//        onCreateOptionsMenu(requireActivity(), requireActivity().getMenuInflater());
    }
}
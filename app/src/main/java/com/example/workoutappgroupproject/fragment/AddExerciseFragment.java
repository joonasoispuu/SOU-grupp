package com.example.workoutappgroupproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.FragmentAddExerciseBinding;

public class AddExerciseFragment extends Fragment {

    FragmentAddExerciseBinding binding;
    ConstraintLayout mainView;
    ActionBar actionBar;

    private EditText etName, etQuantity;
    private NumberPicker ntPicker;
    private Button btnNewExercise;

    public static final String EXTRA_NAME = "com.example.workoutappgroupproject.fragment.EXTRA_NAME";
    public static final String EXTRA_QUANTITY = "com.example.workoutappgroupproject.fragment.EXTRA_QUANTITY";
    public static final String EXTRA_TIME = "com.example.workoutappgroupproject.fragment.EXTRA_TIME";
    public static final String EXTRA_ID = "com.example.workoutappgroupproject.fragment.EXTRA_ID";

    private static final int RESULT_EDIT = 200;
    public static final int RESULT_SAVE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddExerciseBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        mainView = binding.mainView;

        etName = binding.etExerciseName;
        etQuantity = binding.etExerciseQuantity;
        ntPicker = binding.ExerciseTimePicker;
        ntPicker.setMinValue(0);
        ntPicker.setMaxValue(300);

        btnNewExercise = binding.btnNewExercise;

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        onFragmentResult();

        btnNewExercise.setOnClickListener(this::OnDoneSaveExercise);
    }

    public void OnDoneSaveExercise(View view) {
        saveExercise();
    }

    private void saveExercise(){
        if (!validateName() | !validateQuantityTime()){
            // cancel
            return;
        }
        String name = etName.getText().toString().trim();

        int quantity;
        if(etQuantity.getText().toString().isEmpty()){
            quantity = 0;
        } else{
            try {
                quantity = Integer.parseInt(etQuantity.getText().toString());
            } catch (NumberFormatException nfe) {
                quantity = -1;
                System.out.println(getString(R.string.err_nfe));
            }
        }
        // prevent time 0 and quantity 0 <- bug fixed
        if (ntPicker.getValue() == 0 && quantity == 0) {
            Toast.makeText(requireContext(), getString(R.string.err_incorrect_time_quantity), Toast.LENGTH_SHORT).show();
            return;
        } else if (quantity == -1) {
            etQuantity.setError(getString(R.string.err_field_nfe));
            Toast.makeText(requireContext(), getString(R.string.err_field_nfe), Toast.LENGTH_SHORT).show();
            return;
        }
        int time = ntPicker.getValue();

        CustomFragment customFragment = new CustomFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putInt(EXTRA_QUANTITY, quantity);
        bundle.putInt(EXTRA_TIME, time);

        Bundle bundleGet = getArguments();
        int id = -1;
        if (bundleGet != null) {
            id = bundleGet.getInt(EXTRA_ID, -1);
        }
        if (id != -1) {
            bundle.putInt(EXTRA_ID, id);
            // result edit
            bundle.putInt("addexercise_result", RESULT_EDIT);
        } else {
            // result save
            bundle.putInt("addexercise_result", RESULT_SAVE);
        }
        System.out.println("addexercise_result: "+bundle.getInt("addexercise_result"));
        customFragment.setArguments(bundle);
        replaceFragment(customFragment,2,true);
    }

    private boolean validateQuantityTime() {
        if (etQuantity.getText().toString().isEmpty() && ntPicker.getValue()==0){
            Toast.makeText(requireContext(), getString(R.string.exercise_values_missing), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateName() {
        String nameInput = etName.getText().toString().trim();
        if (nameInput.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.exercise_name_missing), Toast.LENGTH_SHORT).show();
            return false;
        } else if (nameInput.length() > 20) {
            Toast.makeText(requireContext(), getString(R.string.exercise_too_long), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void onFragmentResult() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_ID)){
                if (actionBar != null) {
                    actionBar.setTitle(getString(R.string.edit_exercise));
                }
                etName.setText(bundle.getString(EXTRA_NAME));
                etQuantity.setText(String.valueOf(bundle.getInt(EXTRA_QUANTITY, 1)));
                ntPicker.setValue(bundle.getInt(EXTRA_TIME, 1));
            } else {
                if (actionBar != null) {
                    actionBar.setTitle(getString(R.string.create_exercise));
                }
            }
        }
    }

    public void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
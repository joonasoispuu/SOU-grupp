package com.example.workoutappgroupproject.fragment;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.ExerciseActivity;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFragment extends Fragment {
    List<Exercise> exercisesList = new ArrayList<>();
    private static String type = null;
    private ExerciseActivity exerciseActivity;
    private ExerciseViewModel exerciseViewModel;
    TextView txtName, txtQuantity, txtTime;
    static int ID = -1;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXERCISE_ID = "com.example.workoutappgroupproject.activity.EXERCISE_ID";

    public ExerciseFragment() {
        // Required empty public constructor
    }

    public ExerciseFragment(String type, int firstID) {
        this.type = type;
        ID = firstID;
    }

    public static ExerciseFragment newInstance(int id) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(EXERCISE_ID, id);
//        ID = id;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exerciseViewModel = new ViewModelProvider(getActivity()).get(ExerciseViewModel.class);
        exerciseViewModel.getAllExercisesByType(type).observe(getViewLifecycleOwner(),exercises -> {
            System.out.println("INTENT ID: "+ID);
            int id = 0;
            if (getArguments() != null) {
                // get data
                id = getArguments().getInt(EXERCISE_ID, 0);
            }

            if (ID < exercises.size()) {
                String name = exercises.get(id).getName();
                int quantity = exercises.get(id).getQuantity();
                int time = exercises.get(id).getTime();
                System.out.println(" "+exercises.get(id).getId()+" "+name+" "+quantity+" "+time);

                txtName = view.findViewById(R.id.txtName);
                txtName.setText(name);
                txtQuantity = view.findViewById(R.id.txtQuantity);
                if (quantity > 0) txtQuantity.setText(quantity+" "+getString(R.string.quantity_icon));
                else txtQuantity.setText("");
                txtTime = view.findViewById(R.id.txtTime);
                if (time > 0) txtTime.setText(quantity+" "+getString(R.string.time_icon));
                else txtTime.setText("");
            }
        });

        view.findViewById(R.id.btnDone).setOnClickListener(view1 -> {
            exerciseViewModel.getAllExercisesByType(type).observe(getViewLifecycleOwner(),exercises -> {
                boolean dirDown = true;
                if (ID < 0) {
                    // check if all exercises done
                    return;
                } else if (ID < exercises.size()-1) {
                    ID++;
                } else {
                    // cancel new instance creation
                    Toast.makeText(getActivity(),"All exercises finished!",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    return;

                    // for reset btn
//                    if (ID > 0) {
//                        ID--;
//                        dirDown = false;
//                    }
//                    else {
//                        getActivity().finish();
//                        return;
//                    }
                }
                ExerciseFragment newInstance = newInstance(ID);
                if (dirDown) replaceFragment(newInstance,2,false);
                else replaceFragment(newInstance,-2,false);
            });
        });
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getFragmentManager();
        if (fragmentManager == null) return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);
        else if (dir == -2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);

        exerciseActivity = (ExerciseActivity) getActivity();
        //Below is where you get a variable from the main activity
        int host = exerciseActivity.findViewById(R.id.mainView).getId();

        fragmentTransaction.replace(host, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
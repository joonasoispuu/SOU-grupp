package com.example.workoutappgroupproject.fragment;

import android.app.Application;
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
import android.widget.TextView;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.ExerciseDB.ExerciseParcelable;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.ExerciseActivity;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;

import java.util.ArrayList;
import java.util.List;

public class ExerciseFragment extends Fragment {
    private static String type = null;
    private ExerciseActivity exerciseActivity;
    private ExerciseViewModel exerciseViewModel;
    TextView txtName;
    int ID = -1;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exerciseViewModel = new ViewModelProvider(getActivity()).get(ExerciseViewModel.class);
        List<Exercise> exercisesList = new ArrayList<>();
        exerciseViewModel.getAllExercisesByType(type).observe(getViewLifecycleOwner(),exercises -> {
            exercisesList.addAll(exercises);

            if (getArguments() != null) {
                ID = getArguments().getInt(EXERCISE_ID);
                // has exercises
                System.out.println("EXERCISE ID: "+ID);
                System.out.println("EXERCISE NAME: "+exercisesList.get(ID).getName());
            } else {
                // no exercises given
                System.out.println("EXERCISE ID: no exercises found!");
            }
        });

//        txtName = view.findViewById(R.id.txtName);
//
//        for (Exercise e:
//                exercisesList) {
//            System.out.println("ID: "+e.getId());
//        }
//        System.out.println("No exercises found for type: "+type);
        view.findViewById(R.id.btnDone).setOnClickListener(view1 -> {
            if (getArguments() != null) {
                // check if id matches
                while (ID != exercisesList.get(ID-1).getId()) {
                    ID++;
                }
                ExerciseFragment newInstance = newInstance(ID);
                replaceFragment(newInstance,2,false);
            } else {
                ExerciseFragment newInstance = newInstance(1);
                replaceFragment(newInstance,2,false);
            }
        });
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getFragmentManager();
        if (fragmentManager == null) return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);

        exerciseActivity = (ExerciseActivity) getActivity();
        //Below is where you get a variable from the main activity
        int host = exerciseActivity.findViewById(R.id.mainView).getId();

        fragmentTransaction.replace(host, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
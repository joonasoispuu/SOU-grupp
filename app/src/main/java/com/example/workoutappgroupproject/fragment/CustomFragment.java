package com.example.workoutappgroupproject.fragment;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.ExerciseDB.ExerciseAdapter;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.AddExercisesActivity;
import com.example.workoutappgroupproject.activity.CustomActivity;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CustomFragment extends Fragment {

    private ExerciseViewModel exerciseViewModel;
    CoordinatorLayout myCoordinatorMain;
    private static final int RESULT_EDIT = 200;
    public static final int RESULT_SAVE = 100;
    public static String myType = "CUSTOM";
    List<Exercise> myList;

    public CustomFragment() {
        // Required empty public constructor
    }

    public CustomFragment(String myType) {
        CustomFragment.myType = myType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void backToMain() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //reset the menu at top
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            setHasOptionsMenu(true);
            actionBar.setTitle(myType+" Exercises");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setEnabled(false);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == RESULT_SAVE){
                        Intent resultData = result.getData();
                        if(resultData != null){
                            String name = resultData.getStringExtra(AddExercisesActivity.EXTRA_NAME);
                            int quantity = resultData.getIntExtra(AddExercisesActivity.EXTRA_QUANTITY, 1);
                            int time = resultData.getIntExtra(AddExercisesActivity.EXTRA_TIME, 1);

                            Exercise exercise = new Exercise(name, quantity, time, myType.toLowerCase());
                            exerciseViewModel.insert(exercise);
                            Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.save_db), Snackbar.LENGTH_SHORT).show();

                        }else{
                            Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.save_err), Snackbar.LENGTH_SHORT).show();
                        }
                    }else if(result.getResultCode() == RESULT_EDIT){
                        Intent resultData = result.getData();
                        if(resultData != null){
                            int id = resultData.getIntExtra(AddExercisesActivity.EXTRA_ID, -1);
                            if(id == -1){
                                Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.exercise_err),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                            String name = resultData.getStringExtra(AddExercisesActivity.EXTRA_NAME);
                            int quantity = resultData.getIntExtra(AddExercisesActivity.EXTRA_QUANTITY, 0);
                            int time = resultData.getIntExtra(AddExercisesActivity.EXTRA_TIME, 0);
                            Exercise exercise = new Exercise(name,quantity,time,myType.toLowerCase());
                            exercise.setId(id);
                            exerciseViewModel.update(exercise);

                        }else{
                            Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.updated),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }else{
                        Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.exercise_creator_closed),
                                Snackbar.LENGTH_SHORT).show();
                    }
//                    actionBar.setTitle("Custom Exercises");
                }
        );

        myCoordinatorMain = view.findViewById(R.id.myCoordinatorMain);

        view.findViewById(R.id.fabNewExercise).setOnClickListener(view1 ->{
            Intent intent = new Intent(requireActivity(), AddExercisesActivity.class);
            activityResultLauncher.launch(intent);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_exercise_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        final ExerciseAdapter adapter = new ExerciseAdapter();
        recyclerView.setAdapter(adapter);

        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        exerciseViewModel.getAllExercisesByType(myType.toLowerCase()).observe(getViewLifecycleOwner(), adapter::submitList);
        exerciseViewModel.getAllExercisesByType(myType.toLowerCase()).observe(getViewLifecycleOwner(), exercises -> {
            if (exercises.size() < 1) {
                // empty
            }
            this.myList = exercises;
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                exerciseViewModel.delete(adapter.getExercisePosition(viewHolder.getAbsoluteAdapterPosition()));
                Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.exercise_deleted), Snackbar.LENGTH_SHORT).show();
                invalidateOptionsMenu(requireActivity());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(exercise -> {
            Intent intent = new Intent(requireActivity(), AddExercisesActivity.class);
//            Bundle b = new Bundle();
            intent.putExtra(AddExercisesActivity.EXTRA_NAME, exercise.getName());
            intent.putExtra(AddExercisesActivity.EXTRA_TIME, exercise.getTime());
            intent.putExtra(AddExercisesActivity.EXTRA_QUANTITY, exercise.getQuantity());
            intent.putExtra(AddExercisesActivity.EXTRA_ID, exercise.getId());
//            intent.putExtras(b);
            activityResultLauncher.launch(intent);
        });
    }

    // add menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_all_exercises,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_exercise){
            deleteExercises();
        } else {
            backToMain();
        }
        return false;
    }

    private void deleteExercises() {
        // check if exercise db is not empty
        if (myList.size() < 1) {
            Snackbar.make(myCoordinatorMain, getString(R.string.err_deleteall), Snackbar.LENGTH_SHORT).show();
        } else {
            exerciseViewModel.deleteAllExerciseByType(myType.toLowerCase());
            Snackbar.make(myCoordinatorMain, getString(R.string.exercises_deleted), Snackbar.LENGTH_SHORT).show();
        }
    }

}
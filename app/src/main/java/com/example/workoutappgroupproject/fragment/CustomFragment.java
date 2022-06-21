package com.example.workoutappgroupproject.fragment;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.ExerciseDB.ExerciseAdapter;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CustomFragment extends Fragment {

    private ExerciseViewModel exerciseViewModel;
    CoordinatorLayout myCoordinatorMain;
    private static final int RESULT_EDIT = 200;
    public static final int RESULT_SAVE = 100;
    public static String myType = "";
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMain();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void backToMain() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
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
            String title = "";
            switch (myType) {
                case "sixpack":
                    title = getString(R.string.sixpack_exercises);
                    break;
                case "armsandchest":
                    title = getString(R.string.armsandchest_exercises);
                    break;
                case "custom":
                    title = getString(R.string.custom_exercises);
                    break;
            }
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setEnabled(false);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        myCoordinatorMain = view.findViewById(R.id.myCoordinatorMain);

        view.findViewById(R.id.fabNewExercise).setOnClickListener(view1 ->{
            replaceFragment(new AddExerciseFragment(), 2, true);
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

        onFragmentResult();

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

            AddExerciseFragment exerciseFragment = new AddExerciseFragment();
            Bundle bundle = new Bundle();
            bundle.putString(AddExerciseFragment.EXTRA_NAME, exercise.getName());
            bundle.putInt(AddExerciseFragment.EXTRA_TIME, exercise.getTime());
            bundle.putInt(AddExerciseFragment.EXTRA_QUANTITY, exercise.getQuantity());
            bundle.putInt(AddExerciseFragment.EXTRA_ID, exercise.getId());

            exerciseFragment.setArguments(bundle);
            replaceFragment(exerciseFragment, 2, true);
        });
    }

    // get fragment result from arguments
    private void onFragmentResult() {
        View view = null;
        if (getView() != null) view = getView();

        int result;
        Bundle bundle = getArguments();
        if (bundle != null) {
            Snackbar snackbar = null;
            if (bundle.containsKey("addexercise_result")){
                result = getArguments().getInt("addexercise_result");
                if(result == RESULT_SAVE){

                    String name = bundle.getString(AddExerciseFragment.EXTRA_NAME);
                    int quantity = bundle.getInt(AddExerciseFragment.EXTRA_QUANTITY, 1);
                    int time = bundle.getInt(AddExerciseFragment.EXTRA_TIME, 1);

                    Exercise exercise = new Exercise(name, quantity, time, myType.toLowerCase());
                    exerciseViewModel.insert(exercise);
                    Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.save_db), Snackbar.LENGTH_SHORT).show();

                } else if(result == RESULT_EDIT){

                    int id = bundle.getInt(AddExerciseFragment.EXTRA_ID, -1);
                    if(id == -1){
                        Snackbar.make(view.findViewById(R.id.myCoordinatorMain), getString(R.string.exercise_err),
                                Snackbar.LENGTH_SHORT).show();
                    }
                    String name = bundle.getString(AddExerciseFragment.EXTRA_NAME);
                    int quantity = bundle.getInt(AddExerciseFragment.EXTRA_QUANTITY, 0);
                    int time = bundle.getInt(AddExerciseFragment.EXTRA_TIME, 0);
                    Exercise exercise = new Exercise(name,quantity,time,myType.toLowerCase());
                    exercise.setId(id);
                    exerciseViewModel.update(exercise);

                } else {
                    // error!
                }
                getArguments().clear();
            }
        }
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

    public void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
package com.example.workoutappgroupproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.FragmentTrainBinding;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class TrainFragment extends Fragment {

    FragmentTrainBinding binding;
    ConstraintLayout mainView;
    private List<Exercise> exercisesSixpack;
    private List<Exercise> exercisesArmsandChest;
    private List<Exercise> exercisesCustom;
    final static String[] TAGS = {
            "sixpack",
            "armsandchest",
            "custom",
            "addcustom",
    };
    final static int[] VIEW_IDS = {
            R.id.txtSixpack,
            R.id.txtArmsandChest,
            R.id.txtCustom,
            R.id.txtAddCustom,
    };
    private static final int RESULT_NOT_SUCCESS = 200;
    public static final int RESULT_SUCCESS = 100;

    public TrainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMain();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // back to home
    private void backToMain() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        replaceFragment(new ProfileFragment(),-1,false);
//        bottomNavigationView.getMenu().getItem(0).setEnabled(true);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
//        bottomNavigationView.getMenu().getItem(2).setEnabled(true);
        bottomNavigationView.setSelectedItemId(R.id.profileFragment); // select bottom nav bar item
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTrainBinding.inflate(inflater,container,false);
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_train, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExerciseViewModel exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        // get Exercise List by Type -> save each list into variable
        exerciseViewModel.getAllExercisesByType(getString(R.string.sixpack)).observe(getViewLifecycleOwner(), exercisesSixpack -> {
            this.exercisesSixpack = exercisesSixpack;
        });
        exerciseViewModel.getAllExercisesByType(getString(R.string.armsandchest)).observe(getViewLifecycleOwner(), exercisesArmsandChest -> {
            this.exercisesArmsandChest = exercisesArmsandChest;
        });
        exerciseViewModel.getAllExercisesByType(getString(R.string.custom)).observe(getViewLifecycleOwner(), exercisesCustom -> {
            this.exercisesCustom = exercisesCustom;
        });

        //reset the menu at top
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            setHasOptionsMenu(true);
            actionBar.setTitle(R.string.menu_train);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setEnabled(true);
        bottomNavigationView.getMenu().getItem(1).setEnabled(true);
        bottomNavigationView.getMenu().getItem(2).setEnabled(true);

        mainView = binding.mainView;
        onFragmentResult();

        for (int i=0; i<VIEW_IDS.length; i++){
            TextView textView = binding.gridLayout.findViewById(VIEW_IDS[i]);
            textView.setTag(TAGS[i]);
            textView.setOnClickListener(this::onChosenExercise);
        }
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

    //On TrainFragment user can choose what exercise do to by having the fragment replaced
    public void onChosenExercise(View view) {
        String tag = view.getTag().toString().toLowerCase();
        List<Exercise> myList = null;
        System.out.println("EXERCISE TAG: "+tag);
        // set up myList based on TAG (Exercise Type)
        if (tag.equals(TAGS[0])) myList = exercisesSixpack;
        if (tag.equals(TAGS[1])) myList = exercisesArmsandChest;
        if (tag.equals(TAGS[2])) myList = exercisesCustom;
        // new Exercise by type
        if (myList != null) {
            selectSession(myList,tag);
            return;
        }
        // add custom
        CustomFragment customFragment = new CustomFragment("custom");
        replaceFragment(customFragment, 1, true);
    }

    // select exercise session
    private void selectSession(List<Exercise> myList, String tag) {
        int id = 0;
        System.out.println("SIZE: "+myList.size());

        if (myList.size() < 1) { // list empty
            Toast.makeText(requireContext(), R.string.err_session_empty,Toast.LENGTH_SHORT).show();
            // add custom
            String myType = tag.toLowerCase();
            CustomFragment customFragment = new CustomFragment(myType);
            replaceFragment(customFragment, 1, true);
            return;
        }
        int ID = id;
        String type = tag.toLowerCase();
        int size = myList.size();
        int maxTime = myList.get(ID).getTime();

        System.out.println("ID: "+ID);
        System.out.println("MAX_TIME: "+maxTime);

        ExerciseFragment exerciseFragment = new ExerciseFragment(type,size,0,ID,maxTime);
        replaceFragment(exerciseFragment, 1, false);
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
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
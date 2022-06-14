package com.example.workoutappgroupproject.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.BreakActivity;
import com.example.workoutappgroupproject.activity.ExerciseActivity;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ExerciseFragment extends Fragment {
    private static String type = null;
    private static int size = 0;
    private static int maxTime = 0;
    private ExerciseActivity exerciseActivity;
    private ExerciseViewModel exerciseViewModel;
    private CountDownTimer countDownTimer;
    long time = -1;
    int quantity = -1;
    long timeVar = 0;
    private long mtimeStartinMilliseconds;
    private long mtimeLeftinMilliseconds = -1;
    private boolean timerRunning;
    TextView txtName, txtQuantity, txtTime;
    Button btnDone, btnResetTime, btnPause;
    static int ID = -1;
    static int count = 0;
    long defaultTime;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXERCISE_ID = "com.example.workoutappgroupproject.activity.EXERCISE_ID";
    private static final String EXERCISE_COUNT = "com.example.workoutappgroupproject.activity.EXERCISE_COUNT";
    private static final String EXERCISE_MAX_TIME = "com.example.workoutappgroupproject.activity.EXERCISE_MAX_TIME";
    private static final int RESULT_NOT_SUCCESS = 200;
    public static final int RESULT_SUCCESS = 100;

    ActivityResultLauncher<Intent> activityResultLauncher;
    Bundle mySavedInstanceState = null;

    private int nextId;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    public ExerciseFragment(String type, int size, int count, int firstID, int maxTime) {
        ExerciseFragment.type = type;
        ExerciseFragment.size = size;
        ExerciseFragment.count = count;
        ExerciseFragment.maxTime = maxTime;
        ID = firstID;
    }

    public static ExerciseFragment newInstance(int id, int count, int maxTime) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(EXERCISE_ID, id);
        args.putInt(EXERCISE_COUNT, count);
        args.putInt(EXERCISE_MAX_TIME, maxTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySavedInstanceState = savedInstanceState;

//        System.out.println("TOTAL COUNT: "+size);
//        System.out.println("RELATIVE ID: "+ count);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event

                // cancel timer
                cancelTimer();

                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (timeVar > 0) {
            outState.putLong("time",timeVar);
        } else {
            outState.putLong("time",maxTime);
        }
        outState.putInt("quantity",quantity);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            time = savedInstanceState.getLong("time");
            quantity = savedInstanceState.getInt("quantity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.i("ExerciseFragment","onCreateView!");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onDestroyView() {
        // cancel timer
        cancelTimer();
        super.onDestroyView();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == RESULT_SUCCESS){
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            Toast.makeText(requireContext(),"Break over!",Toast.LENGTH_SHORT).show();
                            restoreData(resultData,view);
                        } else {
                            Toast.makeText(requireContext(),"Did not get data!",Toast.LENGTH_SHORT).show();
                        }

                    }else if(result.getResultCode() == RESULT_NOT_SUCCESS){
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            Toast.makeText(requireContext(),"Break failed!",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(),"Did not get data!",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(requireContext(),"Break cancelled!",Toast.LENGTH_SHORT).show();
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            restoreData(resultData,view);
                        }
                    }
                }
        );

        exerciseViewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);

        txtTime = view.findViewById(R.id.txtTime);
        btnDone = view.findViewById(R.id.btnDone);
        btnPause = view.findViewById(R.id.btnPauseTime);
        btnResetTime = view.findViewById(R.id.btnResetTime);

        txtName = view.findViewById(R.id.txtName);
        txtQuantity = view.findViewById(R.id.txtQuantity);

        initExerciseViewModel(view);

        view.findViewById(R.id.btnResetTime).setOnClickListener(view1 -> resetTimer());
        view.findViewById(R.id.btnPauseTime).setOnClickListener(view1 -> togglepause());
        view.findViewById(R.id.btnDone).setOnClickListener(view1 -> newExercise());
    }

    private void initExerciseViewModel(View view) {
        System.out.println("Exercise: INIT");

        int id = 0;
        if (getArguments() != null) {
            // get argument data
            id = getArguments().getInt(EXERCISE_ID, 0);
            count = getArguments().getInt(EXERCISE_COUNT, 0);
            maxTime = getArguments().getInt(EXERCISE_MAX_TIME,0);
        }

        int finalId = id;

        exerciseViewModel.getAllExercisesByType(type).observe(getViewLifecycleOwner(), exercises -> {
            if (size < 1) {
                txtName.setText(R.string.empty);
                btnPause.setVisibility(View.GONE);
                btnResetTime.setVisibility(View.GONE);
                btnDone.setVisibility(View.GONE);
                return;
            }

            if (ID < exercises.size()-1) {
                nextId = exercises.get(count+1).getId();
                System.out.println("next ID: "+nextId);
            } else {
                System.out.println("next ID: null");
            }
//            System.out.println("Name: "+array[0].getName());

//            int a = exercises.indexOf(finalId);
//            System.out.println(a);

//            for (int i=count; i<array.length-1; i++) {
////                System.out.println("ID: "+array[i].getId());
////                System.out.println("Name: "+array[i].getName());
//                nextId = array[i+1].getId();
//                break;
//            }

//            for (Exercise e:
//                 array) {
//                System.out.println("Name: "+e.getName());
//            }

//            for (int i = list.size(); i>-1; i--) {
//                int current_id = exercises.get(i).getId();
//                int actual_id = exercises.get(finalId).getId();
//                if (current_id == actual_id) {
//                    Iterator<Exercise> iterator = list.iterator();
//                    Exercise nextExercise = iterator.next();
//                }
//            }

//            for (Iterator<Exercise> iterator = list.iterator(); iterator.hasNext(); ) {
//                Exercise e = iterator.next();
//                nextId = e.getId();
//            }
//            for (Exercise e : exerciseList) {
//                if (e.getId() == exercises.get(finalId).getId()){
//                    System.out.println(
//                            "Exercise: {" +
//                                    " id: " + e.getId() + "," +
//                                    " name: " + e.getName() + "," +
//                                    " time: " + e.getTime() + "," +
//                                    " quantity: " + e.getQuantity() +
//                                    " type: " + e.getType() +
//                                    " }"
//                    );
//                    System.out.println(nextId);
//                }
//            }
            if (ID < exercises.size()) {
                // get data
                String exercise_name = exercises.get(finalId).getName();
                long exercise_time = exercises.get(finalId).getTime();
                int exercise_quantity = exercises.get(finalId).getQuantity();

                // set the values to display for user
                if (mySavedInstanceState == null) {
                    // get values from db
                    time = exercise_time;
                    quantity = exercise_quantity;
                }

                // display stuff
                txtName.setText(exercise_name);
                if (quantity > 0) txtQuantity.setText(String.format("%d %s", quantity, getString(R.string.quantity_icon)));
                else txtQuantity.setText("");

                mtimeLeftinMilliseconds = time * 1000;
                updateTimer();
//                resetTimer();
                setupTimer();

                // has quantity, cancel listener
                if (time != 0) return;
                view.findViewById(R.id.mainView).setOnClickListener(view1 -> decreaseQuantity());
            }
        });
    }

    private void newExercise(){
        // cancel timer
        cancelTimer();
        exerciseViewModel.getAllExercisesByType(type).observe(getViewLifecycleOwner(),exercises -> {
            if (ID < 0) {
                // check if all exercises done
                return;
            } else if (ID < exercises.size()-1) {
                ID++;
                maxTime = exercises.get(count+1).getTime();
                count++;
            } else {
                // cancel new instance creation (complete)
                Intent data = new Intent();
                // got id from intent getIntExtra()
                if (count == size-1) {
                    requireActivity().setResult(RESULT_SUCCESS, data);
                } else if (count < size-1) {
                    requireActivity().setResult(RESULT_NOT_SUCCESS, data);
                }
                requireActivity().finish();
                return;
            }

            ExerciseFragment newInstance = newInstance(ID, count, maxTime);
//            Bundle args = new Bundle();
//            args.putInt(EXERCISE_MAX_TIME, maxTime);
//            newInstance.setArguments(args);
            replaceFragment(newInstance,2,false);
        });
    }

    private void cancelTimer() {
        if(countDownTimer!=null){
            System.out.println("killed timer task!");
            countDownTimer.cancel();
        } else {
            System.out.println("can't kill timer task!");
        }
    }

    private void setupTimer() {
        cancelTimer();
        if(time != 0){
            btnDone.setVisibility(View.VISIBLE);
            btnDone.setText(R.string.skip);
            long milliseconds = (time) * 1000;
            setTime(milliseconds);
        }
        else{
            btnDone.setVisibility(View.GONE);
            btnPause.setVisibility(View.GONE);
            btnResetTime.setVisibility(View.GONE);
        }
    }

    private void restoreData(Intent data, View view) {
        System.out.println("restoreData!");
        if (data != null) {
            Bundle b = data.getExtras();
        }
        newExercise();
    }

    private void decreaseQuantity() {
        // check quantity value
        quantity -= 1;
        String text = String.format(getString(R.string.quantity_format), quantity);
        if (quantity > 0) {
            if (time != 0) {
                txtQuantity.setText(text);
                time = maxTime;
                mySavedInstanceState = null;
                setupTimer();
            } else {
                txtQuantity.setText(text);
                time = maxTime;
                mySavedInstanceState = null;
            }
        } else {
//            if ()
            text = String.format(getString(R.string.quantity_format), 0);
            txtQuantity.setText(text);
            // new intent for BreakActivity
            Intent intent = new Intent(requireActivity(), BreakActivity.class);
            Bundle b = new Bundle();
            maxTime = 5;
             b.putLong("time_data", maxTime);
            // b.putInt("quantity_data", quantity);
            intent.putExtras(b);
            Toast.makeText(requireContext(),maxTime+" second Break!",Toast.LENGTH_SHORT).show();
            activityResultLauncher.launch(intent);
        }
    }
    public void togglepause(){
        if (timeVar < 1) return;
        if(timerRunning){
            pauseTimer();
        }
        else{
            startTimer();
        }
    }
    private void pauseTimer(){
        cancelTimer();
        timerRunning = false;
        btnResetTime.setVisibility(View.VISIBLE);
        btnPause.setText(R.string.start);
        System.out.println("paused timer!");
    }
    private void resetTimer(){
        if (btnResetTime != null) {
            btnResetTime.setVisibility(View.INVISIBLE);
        }
        mtimeLeftinMilliseconds = mtimeStartinMilliseconds;
        updateTimer();
    }
    public void setTime(long milliseconds){
        mtimeStartinMilliseconds = milliseconds;
        resetTimer();
        startTimer();
    }
    private void startTimer() {
        System.out.println("start timer!");
        btnPause.setText(R.string.pause);
        btnResetTime.setVisibility(View.INVISIBLE);



        countDownTimer = new CountDownTimer(mtimeLeftinMilliseconds, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mtimeLeftinMilliseconds = millisUntilFinished;
                updateTimer();
                // update time display
//                System.out.println("update timer!");
            }

            @Override
            public void onFinish(){
                timerRunning = false;
                // decrease quantity
                decreaseQuantity();
            }
        }.start();

        timerRunning = true;
    }

    private void updateTimer(){
//        mtimeLeftinMilliseconds = ((mtimeLeftinMilliseconds / 1000)+0)*1000;
        int minutes = (int) (mtimeLeftinMilliseconds / 1000) / 60;
        int seconds = (int) (mtimeLeftinMilliseconds / 1000) % 60;

        String timeLeftText = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        txtTime.setText(timeLeftText);
        timeVar = (mtimeLeftinMilliseconds / 1000);

//        if(timeVar < 1) {
//            decreaseQuantity();
//        }
    }

    private void exerciseTimerDone() {
        // cancel timer
        cancelTimer();

//        System.out.println("quantity: "+quantity);

//        Toast.makeText(requireContext(),"Good job! "+10+" second break",Toast.LENGTH_SHORT).show();

//        activityResultLauncher.launch(intent);

//        if (quantity < 1) {
////            System.out.println("exercises done: "+quantity);
//        } else {
//            Toast.makeText(requireContext(),"Good job! "+30+" second break",Toast.LENGTH_SHORT).show();
//            // new intent for BreakActivity
//            Intent intent = new Intent(requireActivity(), BreakActivity.class);
//            Bundle b = new Bundle();
//            b.putLong("time_data", maxTime);
//            b.putInt("quantity_data", quantity);
//            intent.putExtras(b);
//            decreaseQuantity();
//
////            setupTimer();
////            restoreData(intent,getView());
//        }
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getFragmentManager();
        if (fragmentManager == null) return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);
        exerciseActivity = (ExerciseActivity) requireActivity();
        //Below is where you get a variable from the main activity
        int host = exerciseActivity.findViewById(R.id.mainView).getId();

        fragmentTransaction.replace(host, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
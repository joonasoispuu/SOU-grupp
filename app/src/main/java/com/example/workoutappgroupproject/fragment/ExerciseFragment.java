package com.example.workoutappgroupproject.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.ExerciseActivity;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExerciseFragment extends Fragment {
    private static String type = null;
    private static int size = 0;
    private ExerciseActivity exerciseActivity;
    private ExerciseViewModel exerciseViewModel;
    private CountDownTimer countDownTimer;
    long timeVar;
    private long mtimeStartinMilliseconds;
    private long mtimeLeftinMilliseconds = -1;
    private boolean timerRunning;
    TextView txtName, txtQuantity, txtTime;
    Button btnDone, btnResetTime, btnPause;
    static int ID = -1;
    static int count = 0;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXERCISE_ID = "com.example.workoutappgroupproject.activity.EXERCISE_ID";
    private static final String EXERCISE_COUNT = "com.example.workoutappgroupproject.activity.EXERCISE_COUNT";
    private static final int RESULT_NOT_SUCCESS = 200;
    public static final int RESULT_SUCCESS = 100;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    public ExerciseFragment(String type, int size, int count, int firstID) {
        ExerciseFragment.type = type;
        ExerciseFragment.size = size;
        ExerciseFragment.count = count;
        ID = firstID;

    }

    public static ExerciseFragment newInstance(int id, int count) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(EXERCISE_ID, id);
        args.putInt(EXERCISE_COUNT, count);
//        ID = id;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("---------------------------------------------------------");
        System.out.println("TOTAL COUNT: "+size);
        System.out.println("RELATIVE ID: "+ count);
        if (savedInstanceState != null) {
            System.out.println("TIME: "+ savedInstanceState.getLong("time"));
        } else {
            System.out.println("TIME: null");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("time",timeVar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onDestroyView() {
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        super.onDestroyView();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exerciseViewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
        exerciseViewModel.getAllExercisesByType(type).observe(getViewLifecycleOwner(),exercises -> {

            int id = 0;
            if (getArguments() != null) {
                // get data
                id = getArguments().getInt(EXERCISE_ID, 0);
                count = getArguments().getInt(EXERCISE_COUNT, 0);
            }

            txtTime = view.findViewById(R.id.txtTime);
            btnDone = view.findViewById(R.id.btnDone);
            btnPause = view.findViewById(R.id.btnPauseTime);
            btnResetTime = view.findViewById(R.id.btnResetTime);

            txtName = view.findViewById(R.id.txtName);
            txtQuantity = view.findViewById(R.id.txtQuantity);

            if (size < 1) {
                txtName.setText("empty");
                btnPause.setVisibility(View.GONE);
                btnResetTime.setVisibility(View.GONE);
                btnDone.setVisibility(View.GONE);
                return;
            }

            if (ID < exercises.size()) {
                String name = exercises.get(id).getName();
                int quantity = exercises.get(id).getQuantity();
                //int time;
                long time;
                if (savedInstanceState != null) {
                    time = savedInstanceState.getLong("time");
                } else {
                    time = exercises.get(id).getTime();
                }
                System.out.println(" "+exercises.get(id).getId()+" "+name+" "+quantity+" "+time);
                txtName.setText(name);
                if (quantity > 0) txtQuantity.setText(quantity+" "+getString(R.string.quantity_icon));
                else txtQuantity.setText("");

                if(time != 0){
                    btnDone.setText("skip");
                    long milliseconds = time * 1000;
                    setTime(milliseconds);
                }
                else{
                    btnPause.setVisibility(View.GONE);
                    btnResetTime.setVisibility(View.GONE);
                }
            }
        });

        view.findViewById(R.id.btnResetTime).setOnClickListener(view1 -> resetTimer());
        view.findViewById(R.id.btnPauseTime).setOnClickListener(view1 -> togglepause());
        view.findViewById(R.id.btnDone).setOnClickListener(view1 -> newexercise());
    }

    private void newexercise(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        exerciseViewModel.getAllExercisesByType(type).observe(getViewLifecycleOwner(),exercises -> {
            if (ID < 0) {
                // check if all exercises done
                return;
            } else if (ID < exercises.size()-1) {
                ID++;
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
            ExerciseFragment newInstance = newInstance(ID, count);
            replaceFragment(newInstance,2,false);
        });
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

    public void togglepause(){
        if(timerRunning){
            pauseTimer();
        }
        else{
            startTimer();
        }
    }

    private void pauseTimer(){
        countDownTimer.cancel();
        timerRunning = false;
        btnResetTime.setVisibility(View.VISIBLE);
        btnPause.setText(R.string.start);
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
        btnPause.setText(R.string.pause);
        btnResetTime.setVisibility(View.INVISIBLE);

        countDownTimer = new CountDownTimer(mtimeLeftinMilliseconds, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mtimeLeftinMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish(){
                timerRunning = false;
                newexercise();
            }
        }.start();

        timerRunning = true;
    }

    private void updateTimer(){
        int minutes = (int) (mtimeLeftinMilliseconds / 1000) / 60;
        int seconds = (int) (mtimeLeftinMilliseconds / 1000) % 60;

        String timeLeftText = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        if(txtTime != null) {
            txtTime.setText(timeLeftText);
        }
        timeVar = (mtimeLeftinMilliseconds / 1000);
    }
}
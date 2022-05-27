package com.example.workoutappgroupproject.fragment;

import android.annotation.SuppressLint;
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
    private CountDownTimer countDownTimer;
    private long mtimeStartinMilliseconds;
    private long mEndTime;
    private long mtimeLeftinMilliseconds;
    private boolean timerRunning;
    TextView txtName, txtQuantity, txtTime;
    Button btnDone, btnResetTime, btnPause;
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
//            exercisesList.addAll(exercises);

//            for (int i = 0; i<exercisesList.size(); i++) {
//                System.out.println(" "+exercisesList.get(i).getId() + " "+exercisesList.get(i).getName());
//            }
            System.out.println("INTENT ID: "+ID);
            int id = 0;
            if (getArguments() != null) {
                // get data
                id = getArguments().getInt(EXERCISE_ID, 0);
            } else {
                // first time
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
                btnDone = view.findViewById(R.id.btnDone);
                btnPause = view.findViewById(R.id.btnPauseTime);
                btnResetTime = view.findViewById(R.id.btnResetTime);

                if(time != 0){
                    btnDone.setText("skip");
                    long milliseconds = Long.parseLong(String.valueOf(time)) * 1200;
                    setTime(milliseconds);
                }
                else{
                    btnPause.setVisibility(view.GONE);
                    btnResetTime.setVisibility(view.GONE);
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
            } else {
                // cancel new instance creation
                Toast.makeText(getActivity(),"All exercises finished!",Toast.LENGTH_SHORT).show();
                getActivity().finish();
                return;
            }
            ExerciseFragment newInstance = newInstance(ID);
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
        exerciseActivity = (ExerciseActivity) getActivity();
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
    }

    private void resetTimer(){
        mtimeLeftinMilliseconds = mtimeStartinMilliseconds;
        updateTimer();
    }

    public void setTime(long milliseconds){
        mtimeStartinMilliseconds = milliseconds;
        resetTimer();
        startTimer();
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mtimeLeftinMilliseconds;

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
        int minutes = (int) ((mtimeLeftinMilliseconds / 1000) % 3600) / 60;
        int seconds = (int) (mtimeLeftinMilliseconds / 1000) % 60;

        String timeLeftText;

        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if(seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        txtTime.setText(timeLeftText);
    }
}
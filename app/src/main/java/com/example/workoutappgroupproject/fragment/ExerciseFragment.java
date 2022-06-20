package com.example.workoutappgroupproject.fragment;

import static android.content.Context.SENSOR_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.BreakActivity;
import com.example.workoutappgroupproject.sensor.ProximityListener;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class ExerciseFragment extends Fragment {
    private static int ID = -1;
    private static String type = null;
    private static int size = 0;
    private static int maxTime = 0;
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
    static int count = 0;
    private List<Exercise> exercises;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String EXERCISE_ID = "com.example.workoutappgroupproject.activity.EXERCISE_ID";
    private static final String EXERCISE_COUNT = "com.example.workoutappgroupproject.activity.EXERCISE_COUNT";
    private static final String EXERCISE_MAX_TIME = "com.example.workoutappgroupproject.activity.EXERCISE_MAX_TIME";
    private static final int RESULT_NOT_SUCCESS = 200;
    public static final int RESULT_SUCCESS = 100;

    ActivityResultLauncher<Intent> activityResultLauncher;
    Bundle mySavedInstanceState = null;

    ProximityListener proximityListener;

    private int nextId;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    public ExerciseFragment(String type, int size, int count, int ID, int maxTime) {
        ExerciseFragment.type = type;
        ExerciseFragment.size = size;
        ExerciseFragment.count = count;
        ExerciseFragment.maxTime = maxTime;
        ExerciseFragment.ID = ID;
    }

    public static ExerciseFragment newInstance(int id, int maxTime) {
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMain(true);
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
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onPause() {
        System.out.println("onPause!");
//        if () pauseTimer(true);
        cancelTimer();
        super.onPause();
    }

    @Override
    public void onStart() {
        System.out.println("onStart!");
//        startTimer();
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        // cancel timer
        System.out.println("on destroy view!");
        cancelTimer();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        System.out.println("onResume!");
        registerSensors();
        super.onResume();
        if (!timerRunning) {
            if (time>0) {
                btnResetTime.setVisibility(View.VISIBLE);
            }
            btnPause.setText(R.string.start);
        }
    }

    @Override
    public void onStop() {
        System.out.println("onStop!");
        unregisterSensors();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy!");
        unregisterSensors();
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //reset the menu at top
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("SESSION..");
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setEnabled(false);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == RESULT_SUCCESS){
                        Intent resultData = result.getData();
                        if (resultData != null) {
//                            Toast.makeText(requireContext(), getString(R.string.break_success),Toast.LENGTH_SHORT).show();
                            restoreData(resultData);
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.no_data),Toast.LENGTH_SHORT).show();
                        }

                    }else if(result.getResultCode() == RESULT_NOT_SUCCESS){
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            Toast.makeText(requireContext(), getString(R.string.break_failed),Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(),getString(R.string.no_data),Toast.LENGTH_SHORT).show();
                        }

                    }else{
//                        Toast.makeText(requireContext(), getString(R.string.break_canceled),Toast.LENGTH_SHORT).show();
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            restoreData(resultData);
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

        view.findViewById(R.id.btnResetTime).setOnClickListener(view1 -> resetTimer(true));
        view.findViewById(R.id.btnPauseTime).setOnClickListener(view1 -> togglepause());
        view.findViewById(R.id.btnDone).setOnClickListener(view1 -> newExercise());
    }

    private void initExerciseViewModel(View view) {
        System.out.println("Exercise: INIT");

        int id = ID;
        if (getArguments() != null) {
            // get argument data
            id = getArguments().getInt(EXERCISE_ID, 0);
            count = getArguments().getInt(EXERCISE_COUNT, 0);
            maxTime = getArguments().getInt(EXERCISE_MAX_TIME,0);
        }
        int finalId = id;

        exerciseViewModel.getAllExercisesByType(type.toLowerCase()).observe(getViewLifecycleOwner(), exercises -> {
            this.exercises = exercises;
            if (size < 1) {
                txtName.setText(R.string.empty);
                btnPause.setVisibility(View.GONE);
                btnResetTime.setVisibility(View.GONE);
                btnDone.setVisibility(View.GONE);
                return;
            }

            if (ID < exercises.size()) {
                // get data
                String exercise_name = exercises.get(finalId).getName();
                long exercise_time = exercises.get(finalId).getTime();
                int exercise_quantity = exercises.get(finalId).getQuantity();

                // set the values to display for user
                if (mySavedInstanceState == null) {
                    // get values from db (reset to default)
                    time = exercise_time;
                    quantity = exercise_quantity;
                }

                // display stuff
                txtName.setText(exercise_name);
                if (quantity > 0) {
                    txtQuantity.setText(String.format(Locale.getDefault(), "%d %s", quantity, getString(R.string.quantity_icon)));
                }
                else {
                    txtQuantity.setText("");
                }
                if (time>0) updateTimer();
                setupTimer();

                // has quantity, cancel listener
                registerSensors();
            }
        });
    }

    // register sensors
    private void registerSensors() {
        // cancel if time 0
        if (time != 0) return;

        SensorManager sensorManager = (SensorManager)requireActivity().getSystemService(SENSOR_SERVICE);
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximityListener == null) {
            proximityListener = new ProximityListener(
                    requireActivity(),
                    requireActivity().findViewById(android.R.id.content).getRootView(),
                    proximitySensor);

            sensorManager.registerListener(proximityListener,proximitySensor,
                    1000 * 1000);
        }
        // listener for each sensor tick
        if (proximityListener != null) {
            proximityListener.setListener(() -> {
                System.out.println("ProximityListener: onTick()");
                decreaseQuantity();
            });
        }
    }

    // unregister sensors
    private void unregisterSensors() {
        SensorManager sensorManager = (SensorManager)requireActivity().getSystemService(SENSOR_SERVICE);
        if (proximityListener != null) {
            sensorManager.unregisterListener(proximityListener);
            proximityListener.stopSelf();
            proximityListener = null;
        }
    }

    private void newExercise(){
        // cancel timer
        cancelTimer();
        exerciseViewModel.getAllExercisesByType(type.toLowerCase()).observe(getViewLifecycleOwner(),exercises -> {
            if (ID < 0) {
                // check if all exercises done
                return;
            } else if (ID < exercises.size()-1) {
                // next exercise selection (by ID & count)
                maxTime = exercises.get(ID+1).getTime();
                ID++;
//                maxTime = exercises.get(count+1).getTime();
                count++;
            } else {
//                // cancel new instance creation (complete)
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                fragmentManager.popBackStack();
//                requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//                replaceFragment(new ProfileFragment(),-1,false);
                backToMain(false);
                return;
            }
            ExerciseFragment newInstance = newInstance(ID, maxTime);
            replaceFragment(newInstance,2,false);
        });
    }

    // back to home
    private void backToMain(boolean backPressed) {
        cancelTimer();
        unregisterSensors();

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        SettingsFragment settingsFragment = new SettingsFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        TrainFragment trainFragment = new TrainFragment();
        Bundle bundle = new Bundle();

        // set result
        if (!backPressed){
            if (ID == size-1) {
                // session successful
                bundle.putInt("session_result", RESULT_SUCCESS);
            } else if (ID < size-1) {
                // session failed
                bundle.putInt("session_result", RESULT_NOT_SUCCESS);
            }
        } else {
            // session canceled
            bundle.putInt("session_result", 300);
        }
        // set session result for fragment
        if (bundle.getInt("session_result") == RESULT_SUCCESS) {
            bottomNavigationView.getMenu().getItem(0).setEnabled(true);
            bottomNavigationView.getMenu().getItem(1).setEnabled(true);
            bottomNavigationView.getMenu().getItem(2).setEnabled(true);
            bottomNavigationView.setSelectedItemId(R.id.profileFragment); // select bottom nav bar item
            profileFragment.setArguments(bundle); // set arguments
            replaceFragment(profileFragment,-1,false);
        } else {
            trainFragment.setArguments(bundle);
            replaceFragment(trainFragment,-1,false);
        }
    }

    private void cancelTimer() {
        if(countDownTimer!=null){
            System.out.println("killed timer task!");
            countDownTimer.cancel();
            timerRunning = false;
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

    private void restoreData(Intent data) {
        System.out.println("restoreData!");
//        if (data != null) {
//            Bundle b = data.getExtras();
//        }
        newExercise();
    }

    private void decreaseQuantity() {
        // check quantity value
        if (quantity > 0) {
            quantity -= 1;
        } else {
            return;
        }
        String text = String.format(Locale.getDefault(), getString(R.string.quantity_format), quantity);
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
            text = String.format(Locale.getDefault(), getString(R.string.quantity_format), 0);
            txtQuantity.setText(text);
            // new intent for BreakActivity
            if (exercises != null) {
                if (ID < exercises.size()-1){
                    System.out.println("last exercise: false");
                    Intent intent = new Intent(requireActivity(), BreakActivity.class);
                    Bundle b = new Bundle();
//                    maxTime = 5;
                    int breakDuration = 3;
                    b.putLong("time_data", breakDuration);
                    intent.putExtras(b);
                    Toast.makeText(requireContext(),breakDuration+" second Break!",Toast.LENGTH_SHORT).show();
                    activityResultLauncher.launch(intent);
                } else {
                    System.out.println("last exercise: true");
                    restoreData(null);
                }
            }
        }
    }

    public void togglepause(){
        if(timerRunning){
            pauseTimer(true);
        }
        else{
            startTimer();
        }
    }

    private void pauseTimer(boolean enabled){
        if (!enabled) return;

        cancelTimer();
        timerRunning = false;
        if (time>0) {
            btnResetTime.setVisibility(View.VISIBLE);
        }
        btnPause.setText(R.string.start);
        System.out.println("paused timer!");
    }

    private void resetTimer(boolean onClick){
        if (btnResetTime != null) {
            btnResetTime.setVisibility(View.INVISIBLE);
        }

        // set time from memory
        mtimeLeftinMilliseconds = mtimeStartinMilliseconds;

        // reset time to default
        long milliseconds = (maxTime) * 1000;
        if (onClick) mtimeLeftinMilliseconds = milliseconds;
        System.out.println("maxTime (TIME): "+time);
        System.out.println("maxTime (DEFAULT): "+maxTime);
        updateTimer();
    }

    public void setTime(long milliseconds){
        mtimeStartinMilliseconds = milliseconds;
        resetTimer(false);
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
        int minutes = (int) (mtimeLeftinMilliseconds / 1000) / 60;
        int seconds = (int) (mtimeLeftinMilliseconds / 1000) % 60;

        String timeLeftText = String.format(Locale.getDefault(),getString(R.string.timer_format), minutes, seconds);

        txtTime.setText(timeLeftText);
        timeVar = (mtimeLeftinMilliseconds / 1000);
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getFragmentManager();
        if (fragmentManager == null) return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
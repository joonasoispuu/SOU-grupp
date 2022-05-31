package com.example.workoutappgroupproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.workoutappgroupproject.R;

public class AddExercisesActivity extends AppCompatActivity {

    private EditText etName, etQuantity;
    private NumberPicker ntPicker;

    public static final String EXTRA_NAME = "com.example.workoutappgroupproject.activity.EXTRA_NAME";
    public static final String EXTRA_QUANTITY = "com.example.workoutappgroupproject.activity.EXTRA_QUANTITY";
    public static final String EXTRA_TIME = "com.example.workoutappgroupproject.activity.EXTRA_TIME";
    public static final String EXTRA_ID = "com.example.workoutappgroupproject.activity.EXTRA_ID";

    private static final int RESULT_EDIT = 200;
    public static final int RESULT_SAVE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercises);
        etName = findViewById(R.id.etExerciseName);
        etQuantity = findViewById(R.id.etExerciseQuantity);
        ntPicker = findViewById(R.id.ExerciseTimePicker);
        ntPicker.setMinValue(0);
        ntPicker.setMaxValue(300);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            Intent intent = getIntent();
            if(intent.hasExtra(EXTRA_ID)){
                setTitle(getString(R.string.edit));
                etName.setText(intent.getStringExtra(EXTRA_NAME));
                etQuantity.setText(String.valueOf(intent.getIntExtra(EXTRA_QUANTITY, 1)));
                ntPicker.setValue(intent.getIntExtra(EXTRA_TIME, 1));
            }else{
                setTitle("Create Exercise");
            }
            setTitle(getString(R.string.edit));
        }
    }

    public void OnDoneSaveExercise(View view) {
        saveExercise();
    }

    private void saveExercise(){
            if (etName.getText().toString().isEmpty()){
                Toast.makeText(AddExercisesActivity.this, getString(R.string.exercise_name_missing), Toast.LENGTH_SHORT).show(); }
            else if (etQuantity.getText().toString().isEmpty() && ntPicker.getValue()==0){
                Toast.makeText(AddExercisesActivity.this, getString(R.string.exercise_values_missing), Toast.LENGTH_SHORT).show(); }
            else {
                String name = etName.getText().toString().trim();
                int quantity;
                if(etQuantity.getText().toString().isEmpty()){
                    quantity = 0;
                }
                else{
                    quantity = Integer.parseInt(etQuantity.getText().toString());
                }
                int time = ntPicker.getValue();
                Intent data = new Intent();
                data.putExtra(EXTRA_NAME, name);
                data.putExtra(EXTRA_QUANTITY, quantity);
                data.putExtra(EXTRA_TIME, time);
                int id = getIntent().getIntExtra(EXTRA_ID, -1);
                // got id from intent getIntExtra()
                if (id != -1) {
                    data.putExtra(EXTRA_ID, id);
                    setResult(RESULT_EDIT, data);
                } else {
                    setResult(RESULT_EDIT, data);
                }
                finish();
            }
    }
}
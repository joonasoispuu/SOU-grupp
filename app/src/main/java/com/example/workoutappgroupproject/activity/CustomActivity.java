package com.example.workoutappgroupproject.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.ExerciseDB.Exercise;
import com.example.workoutappgroupproject.ExerciseDB.ExerciseAdapter;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;

import java.util.ArrayList;
import java.util.List;

public class CustomActivity extends AppCompatActivity {

    private static final int RESULT_EDIT = 200;
    private ExerciseViewModel exerciseViewModel;
    public static final int RESULT_SAVE = 100;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_SAVE){
                    Intent resultData = result.getData();
                    if(resultData != null){
                        String name = resultData.getStringExtra(AddExercisesActivity.EXTRA_NAME);
                        int quantity = resultData.getIntExtra(AddExercisesActivity.EXTRA_QUANTITY, 1);
                        int time = resultData.getIntExtra(AddExercisesActivity.EXTRA_TIME, 1);

                        Exercise exercise = new Exercise(name, quantity, time, "Custom");
                        exerciseViewModel.insert(exercise);
                        Snackbar.make(findViewById(R.id.myCoordinatorMain), getString(R.string.save_db), Snackbar.LENGTH_SHORT).show();

                    }else{
                        Snackbar.make(findViewById(R.id.myCoordinatorMain), getString(R.string.save_err), Snackbar.LENGTH_SHORT).show();
                    }
                }else if(result.getResultCode() == RESULT_EDIT){
                    Intent resultData = result.getData();
                    if(resultData != null){
                        int id = resultData.getIntExtra(AddExercisesActivity.EXTRA_ID, -1);
                        if(id == -1){
                            Snackbar.make(findViewById(R.id.myCoordinatorMain), getString(R.string.exercise_err),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        String name = resultData.getStringExtra(AddExercisesActivity.EXTRA_NAME);
                        int quantity = resultData.getIntExtra(AddExercisesActivity.EXTRA_QUANTITY, 0);
                        int time = resultData.getIntExtra(AddExercisesActivity.EXTRA_TIME, 0);
                        Exercise exercise = new Exercise(name,quantity,time,"Custom");
                        exercise.setId(id);
                        exerciseViewModel.update(exercise);

                    }else{
                        Snackbar.make(findViewById(R.id.myCoordinatorMain), getString(R.string.updated),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    Snackbar.make(findViewById(R.id.myCoordinatorMain), getString(R.string.exercise_creator_closed),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        findViewById(R.id.fabNewExercise).setOnClickListener(view ->{
            Intent intent = new Intent(CustomActivity.this, AddExercisesActivity.class);
            activityResultLauncher.launch(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_exercise_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final ExerciseAdapter adapter = new ExerciseAdapter();
        recyclerView.setAdapter(adapter);

        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        // myType is the target type to query (has to match the actual type of Exercise instance)
        // TODO: replace myType with the needed type
        exerciseViewModel.getAllExercisesByType("Custom").observe(this, adapter::submitList);

        // get all Exercises with type "Sixpack"
        // TODO: you can remove line 96-110 if u don't need it (meant for testing)
        exerciseViewModel.getAllExercisesByType("Custom").observe(this,exercises -> {
            System.out.println("exerciseList of type count: "+exercises.size());
            for (int i = 0; i<exercises.size(); i++) {
                int id = exercises.get(i).getId();
                String name = exercises.get(i).getName();
                int quantity = exercises.get(i).getQuantity();
                int time = exercises.get(i).getTime();
                String type = exercises.get(i).getType();
                Log.i("exerciseList: ","---EXERCISE---"+"\n"+ "id: "+id+"\n"+ "name: "+name +
                        "\n"+"quantity: "+quantity+"\n"+ "time: "+time+ "\n"+ "type: "+type+"\n");
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                exerciseViewModel.delete(adapter.getExercisePosition(viewHolder.getAbsoluteAdapterPosition()));
                Snackbar.make(findViewById(R.id.myCoordinatorMain), getString(R.string.exercise_deleted), Snackbar.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(exercise -> {
            Intent intent = new Intent(CustomActivity.this, AddExercisesActivity.class);
            intent.putExtra(AddExercisesActivity.EXTRA_NAME, exercise.getName());
            intent.putExtra(AddExercisesActivity.EXTRA_TIME, exercise.getTime());
            intent.putExtra(AddExercisesActivity.EXTRA_QUANTITY, exercise.getQuantity());
            intent.putExtra(AddExercisesActivity.EXTRA_ID, exercise.getId());
            setResult(RESULT_EDIT, intent);
            activityResultLauncher.launch(intent);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete_exercise){
//            exerciseViewModel.deleteAllExercise();
            exerciseViewModel.deleteAllExerciseByType("Custom");
            Snackbar.make(findViewById(R.id.myCoordinatorMain), getString(R.string.exercises_deleted), Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_all_exercises, menu);
        return true;
    }
}

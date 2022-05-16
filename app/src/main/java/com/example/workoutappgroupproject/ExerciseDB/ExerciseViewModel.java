package com.example.workoutappgroupproject.ExerciseDB;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExerciseViewModel extends AndroidViewModel {
    private final ExerciseRepository repository;
    private final LiveData<List<Exercise>> allExercise;

    public ExerciseViewModel(@NonNull Application application){
        super(application);
        repository = new ExerciseRepository(application);
        allExercise = repository.getAllExercises();
    }

    public void insert(Exercise exercise){repository.insert(exercise);}
    public void update(Exercise exercise){repository.update(exercise);}
    public void delete(Exercise exercise){repository.delete(exercise);}
    public void deleteAllExercise(){repository.deleteAllExercise();}

    public LiveData<List<Exercise>> getAllExercises() {
        return allExercise;
    }
}

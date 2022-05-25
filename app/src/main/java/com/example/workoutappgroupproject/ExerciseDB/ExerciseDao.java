package com.example.workoutappgroupproject.ExerciseDB;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Insert
    void insert(Exercise exercise);
    @Update
    void update(Exercise exercise);
    @Delete
    void delete(Exercise exercise);
    @Query("DELETE FROM exercise_table")
    void deleteAllExercise();
    @Query("DELETE FROM exercise_table WHERE type = :myType")
    abstract void deleteAllExerciseByType(String myType);

    @Query("SELECT * FROM exercise_table ORDER BY id")
    LiveData<List<Exercise>> getAllExercises();

    @Query("SELECT * FROM exercise_table WHERE type == :mytype ORDER BY id")
    LiveData<List<Exercise>> getAllExercisesByType(String mytype);
}

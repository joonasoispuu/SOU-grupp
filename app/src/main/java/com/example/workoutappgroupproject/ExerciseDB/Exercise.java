package com.example.workoutappgroupproject.ExerciseDB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercise_table")
public class Exercise {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String name;
    private final int quantity;
    private final int time;

    public Exercise(String name, int quantity, int time){
        this.name = name;
        this.quantity = quantity;
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getQuantity() { return quantity; }

    public int getTime() {
        return time;
    }
}

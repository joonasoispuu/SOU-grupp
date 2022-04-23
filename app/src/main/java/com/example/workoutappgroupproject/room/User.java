package com.example.workoutappgroupproject.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String firstName;
    private final String lastName;
    private final float height;
    private final float weight;
    private final int age;

    public User(String firstName, String lastName, float weight, float height, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.weight = weight;
        this.height = height;
        this.age = age;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }
}

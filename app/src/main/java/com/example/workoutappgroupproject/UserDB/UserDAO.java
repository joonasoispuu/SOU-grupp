package com.example.workoutappgroupproject.UserDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert
    void insert(User user);
    @Update
    void update(User user);
    @Delete
    void delete(User user);
    @Query("DELETE FROM user_table")
    void deleteAllUsers();
    @Query("SELECT * FROM user_table ORDER BY age")
    LiveData<List<User>> getAllUsers();
    @Query("SELECT EXISTS(SELECT * FROM user_table)")
    LiveData<Boolean> isExists();
}

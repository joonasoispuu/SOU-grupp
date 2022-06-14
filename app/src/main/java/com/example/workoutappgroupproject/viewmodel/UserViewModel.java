package com.example.workoutappgroupproject.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.workoutappgroupproject.UserDB.User;
import com.example.workoutappgroupproject.UserDB.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<List<User>> allUsers;
    private final LiveData<Boolean> isExists;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.repository = new UserRepository(application);
        this.allUsers = repository.getAllUsers();
        this.isExists = repository.getIsExists();
    }

    public void insert(User user){ repository.insert(user); }
    public void update(User user){ repository.update(user); }
    public void delete(User user){ repository.delete(user); }
    public void deleteAllUsers(){ repository.deleteAllUsers(); }

    public LiveData<List<User>> getAllUsers() { return allUsers; }
    public LiveData<Boolean> getIsExists() { return isExists; }
}


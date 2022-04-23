package com.example.workoutappgroupproject.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class UserRepository {
    private final UserDAO userDAO;
    private final LiveData<List<User>> allUsers;
    private final LiveData<Boolean> isExists;

    public UserRepository(Application application){
        UserDB db = UserDB.getInstance(application);
        userDAO = db.userDAO();
        allUsers = userDAO.getAllUsers();
        isExists = userDAO.isExists();
    }
    public void insert(User user){ new InsertUserAsyncTask(userDAO).execute(user); }
    public void update(User user){ new UpdateUserAsyncTask(userDAO).execute(user); }
    public void delete(User user){ new DeleteUserAsyncTask(userDAO).execute(user); }
    public void deleteAllUsers(){ new DeleteAllUsersAsyncTask(userDAO).execute(); }
    public LiveData<List<User>> getAllUsers() { return allUsers; }
    public LiveData<Boolean> getIsExists(){ return isExists; }

    private class InsertUserAsyncTask extends AsyncTask<User,Void,Void> {
        private final UserDAO userDAO;
        public InsertUserAsyncTask(UserDAO userDAO) { this.userDAO = userDAO; }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.insert(users[0]);
            return null;
        }
    }
    private class UpdateUserAsyncTask extends AsyncTask<User,Void,Void> {
        private final UserDAO userDAO;
        public UpdateUserAsyncTask(UserDAO userDAO) { this.userDAO = userDAO; }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.update(users[0]);
            return null;
        }
    }
    private class DeleteUserAsyncTask extends AsyncTask<User,Void,Void> {
        private final UserDAO userDAO;
        public DeleteUserAsyncTask(UserDAO userDAO) { this.userDAO = userDAO; }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.delete(users[0]);
            return null;
        }
    }
    private class DeleteAllUsersAsyncTask extends AsyncTask<User,Void,Void> {
        private final UserDAO userDAO;
        public DeleteAllUsersAsyncTask(UserDAO userDAO) { this.userDAO = userDAO; }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.deleteAllUsers();
            return null;
        }
    }
}

package com.example.workoutappgroupproject.room;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class},version = 2,exportSchema = false)
public abstract class UserDB extends RoomDatabase {
    private static final String DB_NAME = "user_db";
    private static UserDB instance;
    public abstract UserDAO userDAO();

    public static synchronized UserDB getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),UserDB.class,DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback).build();
        } return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void,Void,Void> {
        private final UserDAO userDAO;
        public PopulateDBAsyncTask(UserDB db) {
            userDAO = db.userDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // sample data
            //userDAO.insert(new User("First name","Last name",0f,0f,0));
            return null;
        }
    }
}

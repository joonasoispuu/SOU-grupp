package com.example.workoutappgroupproject.ExerciseDB;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Exercise.class},version = 2,exportSchema = false)
public abstract class ExerciseDB extends RoomDatabase {
    private static final String DB_NAME = "exercise_db";
    private static ExerciseDB instance;
    public abstract ExerciseDao exerciseDao();

    public static synchronized ExerciseDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ExerciseDB.class,DB_NAME).fallbackToDestructiveMigration().
                    addCallback(roomCallback).build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ExerciseDao exerciseDao;
        public PopulateDBAsyncTask(ExerciseDB db){exerciseDao = db.exerciseDao();}

        @Override
        protected Void doInBackground(Void... voids) {
            exerciseDao.insert(new Exercise("Push-ups",20,0,"ArmsandChest"));
            exerciseDao.insert(new Exercise("Reps",10,0,"ArmsandChest"));
            exerciseDao.insert(new Exercise("V-ups",15,0,"Sixpack"));
            exerciseDao.insert(new Exercise("Plank",2,20,"Sixpack"));
            exerciseDao.insert(new Exercise("Squats",30,4,"Custom"));
            return null;
        }
    }
}

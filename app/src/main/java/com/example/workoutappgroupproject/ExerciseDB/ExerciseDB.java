package com.example.workoutappgroupproject.ExerciseDB;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

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

            // Sixpack Exercises
            exerciseDao.insert(new Exercise("Jumping Jacks",0,30,"sixpack"));                           // no sensor
            exerciseDao.insert(new Exercise("Heel touch",30,0,"sixpack"));                              // no sensor
            exerciseDao.insert(new Exercise("V-up",16,0,"sixpack"));                                    // sensor
            exerciseDao.insert(new Exercise("Crunches",30,0,"sixpack"));                                // proximity sensor pea alla
            exerciseDao.insert(new Exercise("Flutter kicks",0,40,"sixpack"));                           // no sensor
            exerciseDao.insert(new Exercise("alt V-up",16,0,"sixpack"));                                // sensor
            exerciseDao.insert(new Exercise("Push-up & Rotation",24,0,"sixpack"));                       // sensor
            exerciseDao.insert(new Exercise("Mountain Climber",0,30,"sixpack"));                        // no sensor
            exerciseDao.insert(new Exercise("V-cruch",10,0,"sixpack"));                                 // sensor
            exerciseDao.insert(new Exercise("Seated abs clockwise circles",16,0,"sixpack"));            // sensor
            exerciseDao.insert(new Exercise("Seated abs counterclockwise circles",16,0,"sixpack"));            // sensor
            exerciseDao.insert(new Exercise("Plank",0,60,"sixpack"));            // no sensor

            // Arms & Chest Exercises
            exerciseDao.insert(new Exercise("Jumping Jacks",0,30,"armsandchest"));
            exerciseDao.insert(new Exercise("Arm Circles Clockwise",0,30,"armsandchest"));
            exerciseDao.insert(new Exercise("Arm Circles CounterClockwise",0,30,"armsandchest"));
            exerciseDao.insert(new Exercise("Burpees",10,0,"armsandchest"));
            exerciseDao.insert(new Exercise("Staggered push-ups",10,0,"armsandchest"));
            exerciseDao.insert(new Exercise("Push-up & Rotation",12,0,"armsandchest"));
            exerciseDao.insert(new Exercise("Diamond push-ups",10,0,"armsandchest"));
            exerciseDao.insert(new Exercise("Regular push-ups",12,0,"armsandchest"));
            exerciseDao.insert(new Exercise("Wide arm push-ups",16,0,"armsandchest"));
            exerciseDao.insert(new Exercise("Plank",0,60,"armsandchest"));            // no sensor

            return null;
        }
    }
}

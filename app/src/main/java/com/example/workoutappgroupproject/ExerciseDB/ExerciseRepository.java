package com.example.workoutappgroupproject.ExerciseDB;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ExerciseRepository {
    private final ExerciseDao exerciseDao;
    private final LiveData<List<Exercise>> allExercise;

    public ExerciseRepository(Application application) {
        ExerciseDB db = ExerciseDB.getInstance(application);
        exerciseDao = db.exerciseDao();
        allExercise = exerciseDao.getAllExercises();
    }

    public void insert(Exercise exercise) {
        new InsertExerciseAsyncTask(exerciseDao).execute(exercise);
    }

    public void update(Exercise exercise) {
        new UpdateExerciseAsyncTask(exerciseDao).execute(exercise);
    }
    public void delete(Exercise exercise) {
        new DeleteExerciseAsyncTask(exerciseDao).execute(exercise);
    }

    public void deleteAllExercise() {
        new DeleteAllExerciseAsyncTask(exerciseDao).execute();
    }
    public void deleteAllExerciseByType(String myType) {
        new DeleteAllExerciseByTypeAsyncTask(exerciseDao,myType).execute();
    }

    public LiveData<List<Exercise>> getAllExercises() {return allExercise;}
    public LiveData<List<Exercise>> getAllExercisesByType(String myType) {return exerciseDao.getAllExercisesByType(myType);}

    private static class InsertExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private final ExerciseDao exerciseDao;
        public InsertExerciseAsyncTask(ExerciseDao exerciseDao) {
            this.exerciseDao = exerciseDao;
        }

        @Override
        protected Void doInBackground(Exercise... exercise) {
            exerciseDao.insert(exercise[0]);
            return null;
        }
    }

    public static class UpdateExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private final ExerciseDao exerciseDao;
        public UpdateExerciseAsyncTask(ExerciseDao exerciseDao) {
            this.exerciseDao = exerciseDao;
        }
        @Override
        protected Void doInBackground(Exercise... exercise) {
            exerciseDao.update(exercise[0]);
            return null;
        }
    }

    public static class DeleteExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private final ExerciseDao exerciseDao;
        public DeleteExerciseAsyncTask(ExerciseDao exerciseDao) {
            this.exerciseDao = exerciseDao;
        }

        @Override
        protected Void doInBackground(Exercise... exercise) {
            exerciseDao.delete(exercise[0]);
            return null;
        }
    }

    public static class DeleteAllExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private final ExerciseDao exerciseDao;
        public DeleteAllExerciseAsyncTask(ExerciseDao exerciseDao) {
            this.exerciseDao = exerciseDao;
        }
        @Override
        protected Void doInBackground(Exercise... exercise) {
            exerciseDao.deleteAllExercise();
            return null;
        }
    }

    public static class DeleteAllExerciseByTypeAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private final ExerciseDao exerciseDao;
        String myType;
        public DeleteAllExerciseByTypeAsyncTask(ExerciseDao exerciseDao, String myType) {
            this.exerciseDao = exerciseDao;
            this.myType = myType;
        }
        @Override
        protected Void doInBackground(Exercise... exercise) {
            exerciseDao.deleteAllExerciseByType(myType);
            return null;
        }
    }
}



package com.example.workoutappgroupproject.dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.workoutappgroupproject.activity.MainActivity;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;

public class DialogPrefFragCompat extends PreferenceDialogFragmentCompat {

    private UserViewModel userViewModel;
    private ExerciseViewModel exerciseViewModel;

    public static DialogPrefFragCompat newInstance(String key) {
        final DialogPrefFragCompat fragment = new DialogPrefFragCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);

            userViewModel.deleteAllUsers();
            exerciseViewModel.deleteAllExerciseByType("custom");

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(requireContext());
            settings.edit().clear().apply();

            requireActivity().finish();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        } else {
            // cancel

        }
    }
}

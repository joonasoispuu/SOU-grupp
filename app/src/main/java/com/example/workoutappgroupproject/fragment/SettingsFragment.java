package com.example.workoutappgroupproject.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.workoutappgroupproject.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    boolean notifications_enabled = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        notifications_enabled = sharedPreferences.getBoolean("notifications",false);
        System.out.println("notifications_enabled: "+notifications_enabled);

//        Preference myPref = (Preference) findPreference("notifications");
//        if (myPref != null) {
//            myPref.setOnPreferenceClickListener(preference -> {
//                notifications_enabled = preference.isEnabled();
//                System.out.println("notifications_enabled: "+notifications_enabled);
//                return true;
//            });
//        }
    }
}
package com.example.workoutappgroupproject.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.databinding.FragmentProfileBinding;

public class SettingsFragment extends PreferenceFragmentCompat {

    boolean notifications_enabled = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //reset the menu at top
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            setHasOptionsMenu(true);
            actionBar.setTitle("Settings");
        }

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
//        notifications_enabled = sharedPreferences.getBoolean("notifications",false);
//        System.out.println("notifications_enabled: "+notifications_enabled);

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
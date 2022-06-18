package com.example.workoutappgroupproject.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.workoutappgroupproject.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            setHasOptionsMenu(true);
            actionBar.setTitle("Settings");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String key_theme = getString(R.string.pref_key_theme);
        Preference pref = findPreference(key_theme);
        assert pref != null;
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isChecked = false;
                if (newValue instanceof Boolean)
                    isChecked = (Boolean) newValue;
                if (isChecked) {
                    getPreferenceManager().getSharedPreferences().edit().putBoolean(key_theme, true).apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    getPreferenceManager().getSharedPreferences().edit().putBoolean(key_theme, false).apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });
    }
}
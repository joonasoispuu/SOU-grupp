package com.example.workoutappgroupproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.res.Resources;

import com.example.workoutappgroupproject.LocaleHelper;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.MainActivity;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    Context context;
    Resources resources;

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
        String key_language = getString(R.string.pref_key_language);
        Preference pref = findPreference(key_theme);
        Preference language_pref = findPreference(key_language);

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
                getActivity().finish();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                return true;
            }
        });

        language_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Get selected language
                String selectedLanguage = newValue.toString();
                preference.setSummary(selectedLanguage);
                LocaleHelper.setLocale(getContext(),selectedLanguage);

                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.locale = new Locale(selectedLanguage);
                resources.updateConfiguration(configuration,displayMetrics);


                getActivity().finish();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                return true;
            }
        });
    }
}
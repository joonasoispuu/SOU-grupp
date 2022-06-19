package com.example.workoutappgroupproject.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import android.content.res.Resources;

import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.activity.MainActivity;

import java.util.Locale;

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
        String key_language = getString(R.string.pref_key_language);
        Preference pref = findPreference(key_theme);
        Preference language_pref = findPreference(key_language);
        assert pref != null;

        language_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Get selected language
                String selectedLanguage = newValue.toString();
                preference.setSummary(selectedLanguage);

                // Change language
                Locale locale;
                if (selectedLanguage.equals("Estonian (ee)")) {
                    locale = new Locale("ee");
                }
                else if(selectedLanguage.equals("Finnish (fi)")){
                    locale = new Locale("fi");
                }
                else {
                    locale = new Locale("en");
                }

                Locale.setDefault(locale);
                Resources resources = getContext().getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(locale);
                resources.updateConfiguration(config, resources.getDisplayMetrics());

                return true;
            }
        });


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
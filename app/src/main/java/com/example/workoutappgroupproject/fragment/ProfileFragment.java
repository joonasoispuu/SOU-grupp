package com.example.workoutappgroupproject.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.workoutappgroupproject.R;

public class ProfileFragment extends Fragment {

    public static final String EXTRA_FIRST = "com.nagel.lab5.activity.EXTRA_TITLE";
    public static final String EXTRA_LAST = "com.nagel.lab5.activity.EXTRA_AUTHOR";
    public static final String EXTRA_CONTENT = "com.nagel.lab5.activity.EXTRA_CONTENT";
    public static final String EXTRA_TIME = "com.nagel.lab5.activity.EXTRA_TIME";
    public static final String EXTRA_ID = "com.nagel.lab5.activity.EXTRA_ID";

    public static final int RESULT_SAVE = 100;
    public static final int RESULT_EDIT = 200;

    public ProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
package com.example.workoutappgroupproject.dialog;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.example.workoutappgroupproject.R;

public class CustomDialogPreference extends DialogPreference {

    public CustomDialogPreference(Context context) {
        this(context, null);
    }

    public CustomDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.preference.R.attr.preferenceStyle);
    }

    public CustomDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public CustomDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

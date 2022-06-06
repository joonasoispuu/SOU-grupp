package com.example.workoutappgroupproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.PreferenceCategory;

class MyPreferenceCategory extends PreferenceCategory {

    public MyPreferenceCategory(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean isOnSameScreenAsChildren() {
        return super.isOnSameScreenAsChildren();
    }
}



package com.campus.diary.activity;

import android.os.Bundle;

import com.campus.diary.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        addTitle(getString(R.string.about_us));
        setBackButton();
    }
}

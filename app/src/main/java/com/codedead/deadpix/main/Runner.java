package com.codedead.deadpix.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.codedead.deadpix.domain.LocaleHelper;

public class Runner extends Application {
    @Override
    protected void attachBaseContext(final Context base) {
        SharedPreferences sharedPref = base.getSharedPreferences("deadpixsettings", Context.MODE_PRIVATE);
        super.attachBaseContext(LocaleHelper.onAttach(base, sharedPref.getString("language", "en")));
    }
}

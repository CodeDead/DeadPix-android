package com.codedead.deadpix.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.codedead.deadpix.R;
import com.codedead.deadpix.domain.LocaleHelper;

import java.util.Random;

public class FixActivity extends AppCompatActivity {
    private boolean fixCancelled;
    private FrameLayout frameLayout;
    private int fixDelay = 100;

    private boolean isFixing;
    private static final int doublePressDelay = 500;
    private long lastPressTime;

    private static final Random rnd = new Random();
    private static final int AUTO_HIDE_DELAY_MILLIS = 5000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            frameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            fixCancelled = true;
            finish();

            return false;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        LocaleHelper.setLocale(this, sharedPreferences.getString("language", "en"));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fix);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        frameLayout = findViewById(R.id.frame_fix);

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long pressTime = System.currentTimeMillis();

                if (pressTime - lastPressTime > doublePressDelay) {
                    if (!isFixing) {
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        frameLayout.setBackgroundColor(color);
                    }
                } else {
                    toggle();
                }
                lastPressTime = pressTime;
            }
        });

        findViewById(R.id.BtnCloseFix).setOnTouchListener(mDelayHideTouchListener);

        if (getIntent() != null) {

            fixDelay = getIntent().getIntExtra("delay", 100);

            if (getIntent().getIntExtra("color", -10) != -10) {
                frameLayout.setBackgroundColor(getIntent().getIntExtra("color", 0));
            }

            if (getIntent().getStringExtra("action") != null) {
                if (getIntent().getStringExtra("action").equals("fix")) {
                    fix();
                }
            }
        }

        if (sharedPreferences.getBoolean("fullBrightness", true)) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = 1.0f;
            getWindow().setAttributes(lp);
        }
    }

    private void fix() {
        isFixing = true;

        new CountDownTimer(fixDelay, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                frameLayout.setBackgroundColor(color);
                if (!fixCancelled) {
                    start();
                }
            }
        }.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(300);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        frameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}

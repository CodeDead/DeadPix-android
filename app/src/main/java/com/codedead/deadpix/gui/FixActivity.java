package com.codedead.deadpix.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
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
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        LocaleHelper.setLocale(this, sharedPreferences.getString("language", "en"));

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fix);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        frameLayout = findViewById(R.id.frame_fix);

        frameLayout.setOnClickListener(view -> {
            final long pressTime = System.currentTimeMillis();

            if (pressTime - lastPressTime > doublePressDelay) {
                if (!isFixing && sharedPreferences.getBoolean("changeColours", true)) {
                    final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    frameLayout.setBackgroundColor(color);
                }
            } else {
                toggle();
            }
            lastPressTime = pressTime;
        });

        findViewById(R.id.BtnCloseFix).setOnTouchListener(mDelayHideTouchListener);

        if (getIntent() != null) {

            fixDelay = getIntent().getIntExtra("delay", 100);

            if (getIntent().getIntExtra("color", -10) != -10) {
                frameLayout.setBackgroundColor(getIntent().getIntExtra("color", 0));
            }

            if (getIntent() != null && getIntent().getStringExtra("action") != null && getIntent().getStringExtra("action").equals("fix")) {
                fix();
            }
        }

        if (sharedPreferences.getBoolean("fullBrightness", true)) {
            final WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = 1.0f;
            getWindow().setAttributes(lp);
        }
    }

    private void fix() {
        isFixing = true;

        new CountDownTimer(fixDelay, 1000) {

            @Override
            public void onTick(final long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                frameLayout.setBackgroundColor(color);
                if (!fixCancelled) {
                    start();
                }
            }
        }.start();
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
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
        final ActionBar actionBar = getSupportActionBar();
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

    /**
     * Delay the hiding of the UI
     *
     * @param delayMillis The delay in milliseconds
     */
    private void delayedHide(final int delayMillis) {
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

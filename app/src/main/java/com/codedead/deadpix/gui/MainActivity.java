package com.codedead.deadpix.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadpix.R;
import com.codedead.deadpix.domain.LocaleHelper;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private boolean doubleBackToExitPressedOnce;

    private ViewFlipper viewFlipper;
    private boolean paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        LocaleHelper.setLocale(this, sharedPreferences.getString("language", "en"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewFlipper = findViewById(R.id.ViewFlipper_Main);

        if (savedInstanceState != null) {
            int flipperPosition = savedInstanceState.getInt("TAB_NUMBER");
            viewFlipper.setDisplayedChild(flipperPosition);

            if (flipperPosition > 0) {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(1).getSubMenu().getItem(flipperPosition - 1).getItemId());
            } else {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(flipperPosition).getItemId());
            }
        } else {
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(0).getItemId());
        }

        content_fixer();
        content_help();
        content_about();
        content_settings();

        content_alerts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_donate) {
            openSite("https://codedead.com/?page_id=302");
        }
        return true;
    }

    /**
     * Load the content of the Fixer screen
     */
    private void content_fixer() {
        final FloatingActionButton fabRed = findViewById(R.id.fab_red);
        final FloatingActionButton fabGreen = findViewById(R.id.fab_green);
        final FloatingActionButton fabBlue = findViewById(R.id.fab_blue);
        final FloatingActionButton fabYellow = findViewById(R.id.fab_yellow);
        final FloatingActionButton fabWhite = findViewById(R.id.fab_white);
        final FloatingActionButton fabBlack = findViewById(R.id.fab_black);
        final FloatingActionButton fabOrange = findViewById(R.id.fab_orange);
        final FloatingActionButton fabDarkBlue = findViewById(R.id.fab_darkblue);
        final FloatingActionButton fabPurple = findViewById(R.id.fab_purple);

        final Button btnFix = findViewById(R.id.BtnFix);

        fabRed.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, R.color.red)));

        fabGreen.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, R.color.green)));

        fabBlue.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, R.color.blue)));

        fabYellow.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, R.color.yellow)));

        fabWhite.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, android.R.color.white)));

        fabBlack.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, android.R.color.black)));

        fabOrange.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, R.color.orange)));

        fabDarkBlue.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, R.color.darkblue)));

        fabPurple.setOnClickListener(v -> openLocator(ContextCompat.getColor(MainActivity.this, R.color.purple)));

        btnFix.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FixActivity.class);
            intent.putExtra("action", "fix");
            intent.putExtra("delay", sharedPreferences.getInt("delay", 1));
            startActivity(intent);
        });
    }

    /**
     * Open the dead pixel locator
     *
     * @param color The color that the activity should use
     */
    private void openLocator(int color) {
        final Intent intent = new Intent(this, FixActivity.class);
        intent.putExtra("color", color);
        startActivity(intent);
    }

    /**
     * Load the content of the Help screen
     */
    private void content_help() {
        final Button btnWebsite = findViewById(R.id.BtnWebsite);
        final Button btnSupport = findViewById(R.id.BtnMail);

        btnWebsite.setOnClickListener(v -> openSite("https://codedead.com/"));

        btnSupport.setOnClickListener(v -> ShareCompat.IntentBuilder.from(MainActivity.this)
                .setType("message/rfc822")
                .addEmailTo("admin@codedead.com")
                .setSubject("DeadPix - Android")
                .setText("")
                .setChooserTitle(R.string.text_send_mail)
                .startChooser());
    }

    /**
     * Load the content of the About screen
     */
    private void content_about() {
        final ImageButton btnWebsite = findViewById(R.id.BtnWebsiteAbout);
        final ImageButton btnFacebook = findViewById(R.id.BtnFacebook);
        final ImageButton btnTwitter = findViewById(R.id.BtnTwitter);
        final TextView txtAbout = findViewById(R.id.TxtAbout);
        txtAbout.setMovementMethod(LinkMovementMethod.getInstance());

        btnWebsite.setOnClickListener(v -> openSite("https://codedead.com/"));

        btnFacebook.setOnClickListener(v -> openSite("https://facebook.com/deadlinecodedead"));

        btnTwitter.setOnClickListener(v -> openSite("https://twitter.com/C0DEDEAD"));
    }

    /**
     * Load the content of the Settings screen
     */
    private void content_settings() {
        final Spinner spnLanguage = findViewById(R.id.SpnLanguages);
        final SeekBar sbDelay = findViewById(R.id.SbDelay);
        final CheckBox chbFullBrightness = findViewById(R.id.ChbFullBrightness);
        final CheckBox chbColourClick = findViewById(R.id.ChbChangeColours);
        final Button btnReset = findViewById(R.id.BtnReset);
        final Button btnSave = findViewById(R.id.BtnSave);

        final String lang = sharedPreferences.getString("language", "en");

        assert lang != null;
        switch (lang) {
            default:
            case "en":
                spnLanguage.setSelection(0);
                break;
            case "nl":
                spnLanguage.setSelection(1);
                break;
            case "fr":
                spnLanguage.setSelection(2);
                break;
            case "de":
                spnLanguage.setSelection(3);
                break;
            case "it":
                spnLanguage.setSelection(4);
                break;
            case "es":
                spnLanguage.setSelection(5);
                break;
            case "pt":
                spnLanguage.setSelection(6);
                break;
        }

        sbDelay.setProgress(sharedPreferences.getInt("delay", 100));
        chbFullBrightness.setChecked(sharedPreferences.getBoolean("fullBrightness", true));
        chbColourClick.setChecked(sharedPreferences.getBoolean("changeColours", true));

        btnReset.setOnClickListener(v -> {
            saveSettings(0, 100, true, true);
            spnLanguage.setSelection(0);

            Context c = LocaleHelper.setLocale(getApplicationContext(), sharedPreferences.getString("language", "en"));
            Toast.makeText(MainActivity.this, c.getString(R.string.toast_settins_reset), Toast.LENGTH_SHORT).show();
            recreate();
        });

        btnSave.setOnClickListener(v -> {
            int delay = sbDelay.getProgress();
            if (delay == 0) delay = 1;

            saveSettings(spnLanguage.getSelectedItemPosition(), delay, chbFullBrightness.isChecked(), chbColourClick.isChecked());

            Context c = LocaleHelper.setLocale(getApplicationContext(), sharedPreferences.getString("language", "en"));
            Toast.makeText(MainActivity.this, c.getString(R.string.toast_settings_saved), Toast.LENGTH_SHORT).show();
            recreate();
        });
    }

    /**
     * Load the content of the alerts
     */
    private void content_alerts() {
        if (sharedPreferences.getInt("reviewTimes", 0) >= 2) return;

        final Random rnd = new Random();

        new CountDownTimer(rnd.nextInt(180) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle(R.string.alert_review_title);
                builder1.setMessage(R.string.alert_review_text);
                builder1.setCancelable(true);

                builder1.setPositiveButton(android.R.string.yes, (dialog, id) -> {
                    dialog.cancel();

                    addReview(true);
                    openSite("market://details?id=com.codedead.deadpix");
                });

                builder1.setNegativeButton(R.string.alert_review_never, (dialog, id) -> {
                    dialog.cancel();
                    addReview(true);
                });

                builder1.setNeutralButton(android.R.string.no, (dialog, which) -> {
                    dialog.cancel();
                    addReview(false);
                });

                final AlertDialog alert11 = builder1.create();
                if (!isFinishing() && !paused) {
                    alert11.show();
                } else {
                    cancel();
                    start();
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        paused = false;
        super.onResume();
    }

    /**
     * Save the settings
     *
     * @param languageIndex  The language index that should be saved
     * @param delay          The delay that should be saved
     * @param fullBrightness Set whether screen brightness should be 100% or not
     * @param changeColours  True if colours should switch, otherwise false
     */
    private void saveSettings(int languageIndex, int delay, boolean fullBrightness, boolean changeColours) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        String lang;
        switch (languageIndex) {
            default:
            case 0:
                lang = "en";
                break;
            case 1:
                lang = "nl";
                break;
            case 2:
                lang = "fr";
                break;
            case 3:
                lang = "de";
                break;
            case 4:
                lang = "it";
                break;
            case 5:
                lang = "es";
                break;
            case 6:
                lang = "pt";
                break;
        }

        editor.putString("language", lang);
        editor.putInt("delay", delay);
        editor.putBoolean("fullBrightness", fullBrightness);
        editor.putBoolean("changeColours", changeColours);

        editor.apply();
    }

    /**
     * Method that is used to determine the amount of times the user has been asked for a review.
     * The user can only be asked to do a review 3 times
     *
     * @param done True if the user accepted to leave a review, otherwise false
     */
    private void addReview(boolean done) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (done) {
            editor.putInt("reviewTimes", 3);
        } else {
            editor.putInt("reviewTimes", sharedPreferences.getInt("reviewTimes", 0) + 1);
        }

        editor.apply();
    }

    /**
     * Open a site using an Intent
     * @param site The site that should be opened
     */
    private void openSite(String site) {
        try {
            Uri uriUrl = Uri.parse(site);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        } catch (Exception ex) {
            // Ignored
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("TAB_NUMBER", viewFlipper.getDisplayedChild());
        super.onSaveInstanceState(savedInstanceState);
    }

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
    public void onBackPressed() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.toast_back_again, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.nav_fixer:
                viewFlipper.setDisplayedChild(0);
                break;
            case R.id.nav_help:
                viewFlipper.setDisplayedChild(1);
                break;
            case R.id.nav_about:
                viewFlipper.setDisplayedChild(2);
                break;
            case R.id.nav_settings:
                viewFlipper.setDisplayedChild(3);
                break;
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

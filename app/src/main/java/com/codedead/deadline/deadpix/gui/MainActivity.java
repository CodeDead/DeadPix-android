package com.codedead.deadline.deadpix.gui;

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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadline.deadpix.R;
import com.codedead.deadline.deadpix.domain.LocaleHelper;
import com.tapadoo.alerter.Alerter;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private boolean doubleBackToExitPressedOnce;

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        LocaleHelper.setLocale(this, sharedPreferences.getString("language", "en"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper_Main);

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

    private void content_fixer() {
        FloatingActionButton fabRed = (FloatingActionButton) findViewById(R.id.fab_red);
        FloatingActionButton fabGreen = (FloatingActionButton) findViewById(R.id.fab_green);
        FloatingActionButton fabBlue = (FloatingActionButton) findViewById(R.id.fab_blue);
        FloatingActionButton fabYellow = (FloatingActionButton) findViewById(R.id.fab_yellow);
        FloatingActionButton fabWhite = (FloatingActionButton) findViewById(R.id.fab_white);
        FloatingActionButton fabBlack = (FloatingActionButton) findViewById(R.id.fab_black);
        Button btnFix = (Button) findViewById(R.id.BtnFix);
    }

    private void content_help() {
        Button btnWebsite = (Button) findViewById(R.id.BtnWebsite);
        Button btnSupport = (Button) findViewById(R.id.BtnMail);

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("http://codedead.com/");
            }
        });

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(MainActivity.this)
                        .setType("message/rfc822")
                        .addEmailTo("admin@codedead.com")
                        .setSubject("DeadPix - Android")
                        .setText("")
                        .setChooserTitle(R.string.text_send_mail)
                        .startChooser();
            }
        });
    }

    private void content_about() {
        Button btnWebsite = (Button) findViewById(R.id.BtnWebsiteAbout);
        ImageButton btnFacebook = (ImageButton) findViewById(R.id.BtnFacebook);
        ImageButton btnTwitter = (ImageButton) findViewById(R.id.BtnTwitter);

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("http://codedead.com/");
            }
        });

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("http://codedead.com/");
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("https://facebook.com/deadlinecodedead");
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("https://twitter.com/C0DEDEAD");
            }
        });
    }

    private void content_settings() {
        final Spinner spnLanguage = (Spinner) findViewById(R.id.SpnLanguages);
        Button btnReset = (Button) findViewById(R.id.BtnReset);
        Button btnSave = (Button) findViewById(R.id.BtnSave);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings(0);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings(spnLanguage.getSelectedItemPosition());
            }
        });
    }

    private void content_alerts() {
        if (sharedPreferences.getInt("reviewTimes", 0) >= 2) return;

        Random rnd = new Random();

        new CountDownTimer(rnd.nextInt(180) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Alerter.create(MainActivity.this)
                        .setTitle(R.string.alert_review_title)
                        .setText(R.string.alert_review_text)
                        .setIcon(R.drawable.ic_rate_review)
                        .setBackgroundColor(R.color.colorAccent)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addReview(true);
                                openPlayStore();
                            }
                        })
                        .show();
                addReview(false);
            }
        }.start();
    }

    private void saveSettings(int language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lang;
        switch(language) {
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
        }

        editor.putString("language", lang);
        editor.apply();
    }

    private void addReview(boolean done) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (done) {
            editor.putInt("reviewTimes", 3);
        } else {
            editor.putInt("reviewTimes", sharedPreferences.getInt("reviewTimes", 0) + 1);
        }

        editor.apply();
    }

    private void openPlayStore() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.codedead.deadline.deadpix"));
            startActivity(intent);
        } catch (Exception ignored) {

        }
    }

    private void openSite(String site) {
        try {
            Uri uriUrl = Uri.parse(site);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("TAB_NUMBER", viewFlipper.getDisplayedChild());
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.toast_back_again, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_fixer) {
            viewFlipper.setDisplayedChild(0);
        } else if (id == R.id.nav_help) {
            viewFlipper.setDisplayedChild(1);
        } else if (id == R.id.nav_about) {
            viewFlipper.setDisplayedChild(2);
        } else if (id == R.id.nav_settings) {
            viewFlipper.setDisplayedChild(3);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

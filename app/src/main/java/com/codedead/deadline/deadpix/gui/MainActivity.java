package com.codedead.deadline.deadpix.gui;

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadline.deadpix.R;
import com.codedead.deadline.deadpix.domain.LocaleHelper;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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

    private void content_fixer() {
        FloatingActionButton fabRed = findViewById(R.id.fab_red);
        FloatingActionButton fabGreen = findViewById(R.id.fab_green);
        FloatingActionButton fabBlue = findViewById(R.id.fab_blue);
        FloatingActionButton fabYellow = findViewById(R.id.fab_yellow);
        FloatingActionButton fabWhite = findViewById(R.id.fab_white);
        FloatingActionButton fabBlack = findViewById(R.id.fab_black);
        Button btnFix = findViewById(R.id.BtnFix);

        final Intent intent = new Intent(this, FixActivity.class);

        fabRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("color", "red");
                startActivity(intent);
            }
        });

        fabGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("color", "green");
                startActivity(intent);
            }
        });

        fabBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("color", "blue");
                startActivity(intent);
            }
        });

        fabYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("color", "yellow");
                startActivity(intent);
            }
        });

        fabWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("color", "white");
                startActivity(intent);
            }
        });

        fabBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("color", "black");
                startActivity(intent);
            }
        });

        btnFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("color", "fix");
                intent.putExtra("delay", sharedPreferences.getInt("delay", 1));
                startActivity(intent);
            }
        });
    }

    private void content_help() {
        Button btnWebsite = findViewById(R.id.BtnWebsite);
        Button btnSupport = findViewById(R.id.BtnMail);

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
        Button btnWebsite = findViewById(R.id.BtnWebsiteAbout);
        ImageButton btnFacebook = findViewById(R.id.BtnFacebook);
        ImageButton btnTwitter = findViewById(R.id.BtnTwitter);

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
        final Spinner spnLanguage = findViewById(R.id.SpnLanguages);
        final SeekBar sbDelay = findViewById(R.id.SbDelay);
        Button btnReset = findViewById(R.id.BtnReset);
        Button btnSave = findViewById(R.id.BtnSave);

        String lang = sharedPreferences.getString("language", "en");

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
        }

        sbDelay.setProgress(sharedPreferences.getInt("delay", 100));

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings(0, 100);

                Context c = LocaleHelper.setLocale(getApplicationContext(), sharedPreferences.getString("language", "en"));
                Toast.makeText(MainActivity.this, c.getString(R.string.toast_settins_reset), Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int delay = sbDelay.getProgress();
                if (delay == 0) delay = 1;

                saveSettings(spnLanguage.getSelectedItemPosition(), delay);

                Context c = LocaleHelper.setLocale(getApplicationContext(), sharedPreferences.getString("language", "en"));
                Toast.makeText(MainActivity.this, c.getString(R.string.toast_settings_saved), Toast.LENGTH_SHORT).show();
                recreate();
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle(R.string.alert_review_title);
                builder1.setMessage(R.string.alert_review_text);
                builder1.setCancelable(true);

                builder1.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        addReview(true);
                        openPlayStore();
                    }
                });

                builder1.setNegativeButton(R.string.alert_review_never, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        addReview(true);
                    }
                });

                builder1.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        addReview(false);
                    }
                });

                AlertDialog alert11 = builder1.create();
                if (!isFinishing() &&!paused) {
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

    private void saveSettings(int language, int delay) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lang;
        switch (language) {
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
        }

        editor.putString("language", lang);
        editor.putInt("delay", delay);

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
            ignored.printStackTrace();
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

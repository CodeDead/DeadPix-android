package com.codedead.deadline.deadpix.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadline.deadpix.R;
import com.codedead.deadline.deadpix.domain.LocaleHelper;

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

            if (flipperPosition > 1) {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(1).getSubMenu().getItem(flipperPosition - 1).getItemId());
            } else {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(flipperPosition).getItemId());
            }
        } else {
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(0).getItemId());
        }

        content_fixer();
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
package com.tabbeo.Activities.MainContainer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.instabug.library.IBGInvocationMode;
import com.instabug.library.Instabug;
import com.instabug.library.compat.InstabugActionBarActivity;
import com.tabbeo.R;
import com.tabbeo.TabbeoApp;

public class ActivityMainContainer extends InstabugActionBarActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static int DELAY_LOAD_FRAGMENT = 250; /*ms. When clicked on an item, we wait to let the user see what happens*/
    private DrawerLayout _drawerLayout;
    private ActionBarDrawerToggle _drawerToggle;
    private NavigationView _drawerNavigationView;
    private Handler _handler;
    private MenuItem _previousCheckedItem;
    private AlertDialog _aboutDialog;

    // Fragments
    Fragment _fragmentCourse;
    Fragment _fragmentProfile;
    Fragment _fragmentSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbeo_main_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.maincontainer_toolbar);
        setSupportActionBar(toolbar);

        _handler = new Handler();

        _drawerLayout = (DrawerLayout) findViewById(R.id.maincontainer_drawerlayout);
        _drawerNavigationView = (NavigationView) findViewById(R.id.maincontainer_drawerlayout_navigationview);

        _drawerToggle = new ActionBarDrawerToggle(this, _drawerLayout, toolbar, 0, 0);

        _drawerLayout.setDrawerListener(_drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        _drawerNavigationView.setNavigationItemSelectedListener(this);

        // Let's set the course as the first item
        MenuItem courseItem = _drawerNavigationView.getMenu().findItem(R.id.drawerview_menu_course);
        courseItem.setChecked(true);
        _previousCheckedItem = courseItem;
        navigateToFragment(R.id.drawerview_menu_course);

        // Set the version number
        try {
            TextView versionTextView = (TextView) _drawerNavigationView.getHeaderView(0).findViewById(R.id.drawer_header_version_textview);
            String version = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            versionTextView.setText("Version "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the start state after onRestoreInstanceState has occurred.
        _drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return _drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        _drawerLayout.closeDrawer(GravityCompat.START);

        // We post it delayed
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MenuItem feedbackItem = _drawerNavigationView.getMenu().findItem(R.id.drawerview_menu_feedback);
                MenuItem aboutItem = _drawerNavigationView.getMenu().findItem(R.id.drawerview_menu_about);


                if (menuItem.getItemId() == feedbackItem.getItemId()) {
                    Instabug.invoke(IBGInvocationMode.IBGInvocationModeFeedbackSender);

                }else if(menuItem.getItemId() == aboutItem.getItemId()){
                    // Create the "About" dialog
                    getAboutDialog().show();

                } else { // Checkable items
                    if (_previousCheckedItem != null) {
                        _previousCheckedItem.setChecked(false);
                    }

                    menuItem.setChecked(true);
                    _previousCheckedItem = menuItem;

                    navigateToFragment(menuItem.getItemId());
                }
            }
        }, DELAY_LOAD_FRAGMENT);

        return true;
    }

    private AlertDialog getAboutDialog() {
        if(_aboutDialog != null) return _aboutDialog;

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        ScrollView aboutLayout = (ScrollView) inflater.inflate(R.layout.about, null);

        AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(ActivityMainContainer.this, R.style.TabbeoDialogTheme);
        aboutBuilder.setTitle(R.string.drawerlayout_about).setView(aboutLayout);

        _aboutDialog = aboutBuilder.create();

        return _aboutDialog;
    }

    private void navigateToFragment(int itemId){

        Fragment fragment;
        switch (itemId) {
            case R.id.drawerview_menu_course:
                if (_fragmentCourse == null)
                    _fragmentCourse = new FragmentCourse();
                fragment = _fragmentCourse;
                getSupportActionBar().setTitle(R.string.drawerlayout_course);
                break;
            case R.id.drawerview_menu_profile:
                if (_fragmentProfile == null)
                    _fragmentProfile = new FragmentProfile();
                fragment = _fragmentProfile;
                getSupportActionBar().setTitle(R.string.drawerlayout_profile);
                break;
            case R.id.drawerview_menu_settings:
                if (_fragmentSettings == null)
                    _fragmentSettings = new FragmentSettings();
                fragment = _fragmentSettings;
                getSupportActionBar().setTitle(R.string.drawerlayout_settings);
                break;
            default:
                throw new RuntimeException("Unknown item in the navigation drawer list");
        }

        getFragmentManager().beginTransaction().replace(R.id.maincontainer_fragmentcontainer, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (_drawerLayout.isDrawerOpen(GravityCompat.START)) {
            _drawerLayout.closeDrawer(GravityCompat.START);
        } else if(!_drawerNavigationView.getMenu().findItem(R.id.drawerview_menu_course).isChecked()){
            // We are not in the course fragment. Pressing back will bring you there
            MenuItem courseItem = _drawerNavigationView.getMenu().findItem(R.id.drawerview_menu_course);
            courseItem.setChecked(true);
            if (_previousCheckedItem != null) {
                _previousCheckedItem.setChecked(false);
            }
            _previousCheckedItem = courseItem;
            navigateToFragment(R.id.drawerview_menu_course);

        } else{
            // We are in the course fragment. Pressing back will exit the app
            super.onBackPressed();
        }
    }
}


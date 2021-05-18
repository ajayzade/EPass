package com.rawtalent.epass_admin.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.rawtalent.epass_admin.R;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private ChangeNavigationActivities mChangeActivityFromNavigation;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext =getApplicationContext();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mChangeActivityFromNavigation = new ChangeNavigationActivities();

    }

    private void setNavigation() {
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavigation();
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_account_verification) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startAccountVerificationActivity(mContext);
        } else if (id==R.id.nav_new_application){
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startEpassApplicationActivity(mContext);
        }else if (id==R.id.logout){

        }


        return false;
    }
}
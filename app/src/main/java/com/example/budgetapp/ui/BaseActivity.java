package com.example.budgetapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.budgetapp.R;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupNavigation(@LayoutRes int layoutResID) {

        setContentView(layoutResID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        androidx.appcompat.app.ActionBarDrawerToggle toggle =
                new androidx.appcompat.app.ActionBarDrawerToggle(
                        this, drawerLayout, toolbar,
                        R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                if (!(this instanceof MainActivity)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                drawerLayout.closeDrawers();
                return true;
            }

            if (id == R.id.nav_monthly) {
                if (!(this instanceof MonthlyAggregationActivity)) {
                    startActivity(new Intent(this, MonthlyAggregationActivity.class));
                    finish();
                }
                drawerLayout.closeDrawers();
                return true;
            }

            return false;
        });
    }
}

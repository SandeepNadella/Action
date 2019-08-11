package com.rekam.action;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            View trainView = findViewById(R.id.train_layout);
            View testView = findViewById(R.id.test_layout);
            View aboutView = findViewById(R.id.about_layout);
            trainView.setVisibility(View.INVISIBLE);
            testView.setVisibility(View.INVISIBLE);
            aboutView.setVisibility(View.INVISIBLE);
            switch (item.getItemId()) {
                case R.id.train_dashboard:
                    trainView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.test_dashboard:
                    testView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.about_dashboard:
                    aboutView.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}

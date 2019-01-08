package com.example.mkkuc.project;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.mkkuc.project.adapter.ItemAdapter;
import com.example.mkkuc.project.adapter.ItemModel;
import com.example.mkkuc.project.database.AppDatabase;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.fragments.FindWeatherFragment;
import com.example.mkkuc.project.fragments.HomeFragment;
import com.example.mkkuc.project.fragments.ReadWeatherFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    public static AppDatabase appDatabase;
    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources resources = getResources();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        fragmentManager = getSupportFragmentManager();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "weathersDB").allowMainThreadQueries().build();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null)
                return;
            fragmentManager.beginTransaction().add(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

}

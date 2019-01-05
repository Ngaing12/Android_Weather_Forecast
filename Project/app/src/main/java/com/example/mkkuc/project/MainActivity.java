package com.example.mkkuc.project;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mkkuc.project.adapter.RecyclerAdapter;
import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.database.AppDatabase;
import com.example.mkkuc.project.database.DatabaseHelper;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.fragments.HomeFragment;
import com.example.mkkuc.project.helper.Helper;
import com.example.mkkuc.project.model.OpenWeatherMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity /*implements LocationListener*/ {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageView;

    LocationManager locationManager;
    String provider;
    static double lat, lon;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION = 0;

    public static FragmentManager fragmentManager;
    public static AppDatabase appDatabase;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<String> list;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fragments, which I used
        /*fragmentManager = getSupportFragmentManager();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "weathersDB").allowMainThreadQueries().build();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null)
                return;
            fragmentManager.beginTransaction().add(R.id.fragment_container, new HomeFragment()).commit();
        }*/


        //RecyclerView
        recyclerView = findViewById(R.id.recyclerviewList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list = Arrays.asList(getResources().getStringArray(R.array.android_versions));
        adapter = new RecyclerAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

        //Control
        /*
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
            return;
        }

        //Get Coordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null)
            Log.e("TAG","No Location");
    }

    public void handleWeatherPanelButton(View view){
        Intent intent = new Intent (getApplicationContext(), NewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
            return;
        }
        locationManager.requestLocationUpdates(provider, 600, 10, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        new GetWeather().execute(Common.apiRequest(lat, lon));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public AlertDialog setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
        return dialog;
    }

    private class GetWeather extends AsyncTask<String, Void, String>{
        AlertDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = setProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];

            Helper http = new Helper();
            String stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.contains("Error: Not found city"))
                return;

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);

            String country = openWeatherMap.getSys().getCountry();
            String city = openWeatherMap.getCity();
            String description = openWeatherMap.getWeather().get(0).getDescription();
            String lastUpdate = Common.getDateNow();
            int humidity = openWeatherMap.getMain().getHumidity();
            double temp = openWeatherMap.getMain().getTemp();
            double sunrise = openWeatherMap.getSys().getSunrise();
            double sunset = openWeatherMap.getSys().getSunset();

            txtCity.setText(String.format("%s, %s", city, country));
            txtLastUpdate.setText(String.format("Last Updated: %s", lastUpdate));
            txtDescription.setText(String.format("%s", description));
            txtHumidity.setText(String.format("Humidity: %d%%", humidity));
            txtTime.setText(String.format("Sunrise: %s \n Sunset: %s",
                    Common.unixTimeStampToDateTime(sunrise),
                    Common.unixTimeStampToDateTime(sunset)));
            txtCelsius.setText(String.format("Temperature: %.2f °C", temp));
            Picasso.get()
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

            DatabaseHelper db = new DatabaseHelper(MainActivity.this);

            WeatherEntity weather = new WeatherEntity(
                    country,
                    city,
                    description,
                    lastUpdate,
                    humidity,
                    lat,
                    lon,
                    temp,
                    sunrise,
                    sunset);

            if(!db.isWeatherByCityAndCountry(city, country))
                db.addWeather(weather);
            else
                db.updateWeather(weather);
            dialog.dismiss();
        }
    }*/
}

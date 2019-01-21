package com.example.mkkuc.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mkkuc.project.common.AlertDialogComponent;
import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.common.CountryCodes;
import com.example.mkkuc.project.common.FixDescription;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.helper.Helper;
import com.example.mkkuc.project.model.OpenWeatherMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class CurrentWeatherActivity extends AppCompatActivity implements LocationListener {

    TextView txtConnection, txtCityAndCountry, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageView;
    AlertDialog dialog;
    LocationManager locationManager;
    String provider;
    static double lat, lon;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    Intent intent;
    int MY_PERMISSION = 0;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_weather);
        layout = (LinearLayout) findViewById(R.id.current_w);
        handleLocation();

    }

    private boolean arePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CurrentWeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
            return false;
        }
        return true;
    }

    private boolean isNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }

    public void handleLocation() {
        dialog = new AlertDialogComponent(getResources()).setProgressDialog(this);
        txtConnection = (TextView) findViewById(R.id.txtConnection);
        txtConnection.setText("");
        txtCityAndCountry = (TextView) findViewById(R.id.txtCityAndCountry);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CurrentWeatherActivity.this, new String[]{
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
            Log.e("TAG", "No Location");
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (!arePermissions())
            return;

        if (!isNetworkConnection()) {
            Resources resources = getResources();
            txtConnection.setText(resources.getString(R.string.check_connection));
            return;
        }

        try {
            locationManager.removeUpdates(this);
        } catch (NullPointerException e) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CurrentWeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
            return;
        }

        if (!isNetworkConnection()) {
            txtConnection.setText(R.string.check_connection);
            dialog.dismiss();
            return;
        }

        try {
            locationManager.requestLocationUpdates(provider, 600, 10, this);
        } catch (NullPointerException e) {
            return;
        }
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

    private class GetWeather extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isNetworkConnection()) {
                txtConnection.setText(R.string.check_connection);
                dialog.dismiss();
                return;
            }
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

            if (s == null) {
                dialog.dismiss();
                return;
            }


            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();
            openWeatherMap = gson.fromJson(s, mType);



            String country = openWeatherMap.getSys().getCountry();
            String city = openWeatherMap.getCity();
            String description = openWeatherMap.getWeather().get(0).getDescription();

            description = new FixDescription().fixDescription(description);
            Resources resources = getResources();

            String lastUpdate = Common.getDateNow();
            int humidity = openWeatherMap.getMain().getHumidity();
            double temp = openWeatherMap.getMain().getTemp();
            double sunrise = openWeatherMap.getSys().getSunrise();
            double sunset = openWeatherMap.getSys().getSunset();

            String timeNow = Common.getTimeNow();
            String startTimeParse[] = timeNow.split(":");
            String sunriseTime[] = Common.unixTimeStampToDateTime(sunrise).split(":");
            int nowHour = Integer.parseInt(startTimeParse[0]);
            int nowMinute = Integer.parseInt(startTimeParse[1]);
            int sunriseHour = Integer.parseInt(sunriseTime[0]);
            int sunriseMinute = Integer.parseInt(sunriseTime[1]);
            int hourResultRise = sunriseHour - nowHour;
            int minutesResultRise = sunriseMinute - nowMinute;

            String sunsetTime[] = Common.unixTimeStampToDateTime(sunset).split(":");
            int sunsetHour = Integer.parseInt(sunriseTime[0]);
            int sunsetMinute = Integer.parseInt(sunriseTime[1]);
            int hourResultSet = sunriseHour - nowHour;
           int minutesResultSet = sunriseMinute - nowMinute;

            int color = Color.BLACK;
            boolean isDay = false;
            if(hourResultRise < 0)
                if(hourResultSet > 0)
                    isDay = true;
                else if (hourResultSet == 0 && minutesResultSet >= 0)
                    isDay = true;
            else if (hourResultRise == 0 && minutesResultRise <= 0)
                    isDay = true;

            if(isDay)
            {
                if(description.contains("snow") || description.contains("Snow"))
                    layout.setBackgroundResource(R.drawable.winter);
                else
                    layout.setBackgroundResource(R.drawable.day);
            }
            else
            {
                color = Color.WHITE;
                if(description.contains("snow") || description.contains("Snow"))
                    layout.setBackgroundResource(R.drawable.winter_night);
                else
                    layout.setBackgroundResource(R.drawable.night);
            }
            txtCityAndCountry.setTextColor(color);
            txtLastUpdate.setTextColor(color);
            txtDescription.setTextColor(color);
            txtHumidity.setTextColor(color);
            txtTime.setTextColor(color);
            txtCelsius.setTextColor(color);
            txtCityAndCountry.setText(String.format("%s, %s", city, country));
            txtLastUpdate.setText(String.format("%s: %s",
                    resources.getString(R.string.last_update),
                    lastUpdate));
            txtDescription.setText(String.format("%s", description));
            txtHumidity.setText(String.format("%s: %d%%",
                    resources.getString(R.string.humidity),
                    humidity));
            txtTime.setText(String.format("%s: %s \n%s: %s",
                    resources.getString(R.string.sunrise),
                    Common.unixTimeStampToDateTime(sunrise),
                    resources.getString(R.string.sunset),
                    Common.unixTimeStampToDateTime(sunset)));
            txtCelsius.setText(String.format("%s: %.2f °C",
                    resources.getString(R.string.temperature),
                    temp));
            Picasso.get()
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

            country = new CountryCodes().getCountryName(country);

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

            List<WeatherEntity> list = MainActivity.appDatabase.weatherDao().getWeathers();
            boolean check = false;
            for (WeatherEntity weatherE : list) {
                if (weatherE.getCountry().equals(country)
                        &&
                        weatherE.getCity().equals(city)) {
                    weather.setWeatherID(weatherE.getWeatherID());
                    MainActivity.appDatabase.weatherDao().updateWeather(weather);
                    check = true;
                    break;
                }
            }
            if (!check)
                MainActivity.appDatabase.weatherDao().addWeather(weather);
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.current_weather_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_current:
                dialog = new AlertDialogComponent(getResources()).setProgressDialog(this);
                new GetWeather().execute(Common.apiRequest(lat, lon));
                break;

            case R.id.show_more_details_current:
                intent = new Intent(getApplicationContext(), ShowDetailsActivity.class);
                String _lat = String.format("%.5f", lat);
                String _lon = String.format("%.5f", lon);
                intent.putExtra("lat", _lat);
                intent.putExtra("lon", _lon);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

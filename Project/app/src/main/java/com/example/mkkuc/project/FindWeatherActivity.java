package com.example.mkkuc.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mkkuc.project.adapter.ItemAdapter;
import com.example.mkkuc.project.adapter.ItemModel;
import com.example.mkkuc.project.common.AlertDialogComponent;
import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.common.CountryCodes;
import com.example.mkkuc.project.common.FixDescription;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.fragments.ReadWeatherFragment;
import com.example.mkkuc.project.helper.Helper;
import com.example.mkkuc.project.model.Main;
import com.example.mkkuc.project.model.OpenWeatherMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FindWeatherActivity extends AppCompatActivity {

    TextView txtConnectionF, txtCityAndCountryF, txtLastUpdateF, txtDescriptionF, txtHumidityF, txtTimeF, txtCelsiusF;
    ImageView imageViewF;
    AlertDialog dialog;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    String cityFind;
    String countryFind;

    double lat, lon;

    Intent intent;

    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_weather);
        handleLocation();
    }

    private boolean arePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FindWeatherActivity.this, new String[]{
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
        txtConnectionF = (TextView) findViewById(R.id.txtConnectionF);
        txtConnectionF.setText("");
        txtCityAndCountryF = (TextView) findViewById(R.id.txtCityAndCountryF);
        txtLastUpdateF = (TextView) findViewById(R.id.txtLastUpdateF);
        txtDescriptionF = (TextView) findViewById(R.id.txtDescriptionF);
        txtHumidityF = (TextView) findViewById(R.id.txtHumidityF);
        txtTimeF = (TextView) findViewById(R.id.txtTimeF);
        txtCelsiusF = (TextView) findViewById(R.id.txtCelsiusF);
        imageViewF = (ImageView) findViewById(R.id.imageViewF);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FindWeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
            return;
        }
        Intent intent = getIntent();
        cityFind = intent.getStringExtra("City");
        countryFind = intent.getStringExtra("Country");
        new GetWeather().execute(Common.apiRequest(cityFind, countryFind));
    }

    private class GetWeather extends AsyncTask<String, Void, String> {

        public GetWeather() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isNetworkConnection()) {
                txtConnectionF.setText(R.string.check_connection);
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
            Resources resources = getResources();
            if (s == null) {
                txtConnectionF.setText(resources.getString(R.string.something_went_wrong));
                dialog.dismiss();
            } else {

                Gson gson = new Gson();
                Type mType = new TypeToken<OpenWeatherMap>() {
                }.getType();
                openWeatherMap = gson.fromJson(s, mType);

                String country = openWeatherMap.getSys().getCountry();
                String city = openWeatherMap.getCity();
                String description = openWeatherMap.getWeather().get(0).getDescription();

                description = new FixDescription().fixDescription(description);

                String lastUpdate = Common.getDateNow();
                int humidity = openWeatherMap.getMain().getHumidity();
                double temp = openWeatherMap.getMain().getTemp();
                double sunrise = openWeatherMap.getSys().getSunrise();
                double sunset = openWeatherMap.getSys().getSunset();
                lat = openWeatherMap.getCoord().getLat();
                lon = openWeatherMap.getCoord().getLon();

                txtCityAndCountryF.setText(String.format("%s, %s", city, country));

                txtLastUpdateF.setText(String.format("%s: %s",
                        resources.getString(R.string.last_update),
                        lastUpdate));
                txtDescriptionF.setText(String.format("%s", description));
                txtHumidityF.setText(String.format("%s: %d%%",
                        resources.getString(R.string.humidity),
                        humidity));
                txtTimeF.setText(String.format("%s: %s \n%s: %s",
                        resources.getString(R.string.sunrise),
                        Common.unixTimeStampToDateTime(sunrise),
                        resources.getString(R.string.sunset),
                        Common.unixTimeStampToDateTime(sunset)));
                txtCelsiusF.setText(String.format("%s: %.2f °C",
                        resources.getString(R.string.temperature),
                        temp));
                Picasso.get()
                        .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                        .into(imageViewF);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.find_weather_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_find:
                dialog = new AlertDialogComponent(getResources()).setProgressDialog(this);
                new GetWeather().execute(Common.apiRequest(cityFind, countryFind));
                Resources resources = getResources();
                Toast.makeText(this, resources.getString(R.string.updating_completed), Toast.LENGTH_SHORT).show();
                Log.i("Updated", "Updating completed");
                return true;

            case R.id.show_more_details_find:
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

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
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mkkuc.project.common.AlertDialogComponent;
import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.common.CountryCodes;
import com.example.mkkuc.project.common.FixDescription;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.fragments.ReadWeatherFragment;
import com.example.mkkuc.project.helper.Helper;
import com.example.mkkuc.project.model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LookWeatherActivity extends AppCompatActivity {

    TextView txtConnectionL, txtCityAndCountryL, txtLastUpdateL, txtDescriptionL, txtHumidityL, txtTimeL, txtCelsiusL;
    ImageView imageViewL;
    Intent intent;
    AlertDialog dialog;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    double lat, lon;

    int updateWeatherID;
    String updateCity;
    String updateCountry;


    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_weather);
        Intent intent = getIntent();
        String update = intent.getStringExtra("Update");
        handleLastUpdateWeather();
    }

    private boolean isNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }

    public void handleLastUpdateWeather(){
        dialog = new AlertDialogComponent(getResources()).setProgressDialog(this);
        txtConnectionL = (TextView) findViewById(R.id.txtConnectionL);
        txtConnectionL.setText("");
        txtCityAndCountryL = (TextView) findViewById(R.id.txtCityAndCountryL);
        txtLastUpdateL = (TextView) findViewById(R.id.txtLastUpdateL);
        txtDescriptionL = (TextView) findViewById(R.id.txtDescriptionL);
        txtHumidityL = (TextView) findViewById(R.id.txtHumidityL);
        txtTimeL = (TextView) findViewById(R.id.txtTimeL);
        txtCelsiusL = (TextView) findViewById(R.id.txtCelsiusL);
        imageViewL = (ImageView) findViewById(R.id.imageViewL);

        Intent intent = getIntent();
        int id = 0;
        int stringID = intent.getIntExtra("WeatherID", id);

        updateWeatherID = stringID;

        WeatherEntity weatherEntity = MainActivity.appDatabase.weatherDao().getWeather(updateWeatherID);

        Resources resources = getResources();
        String country = weatherEntity.getCountry();
        String city = weatherEntity.getCity();
        String description = weatherEntity.getDescription();

        description = new FixDescription().fixDescription(description);

        String lastUpdate = Common.getDateNow();
        int humidity = weatherEntity.getHumidity();
        double temp = weatherEntity.getTemp();
        double sunrise = weatherEntity.getSunrise();
        double sunset = weatherEntity.getSunset();
        lat = weatherEntity.getLat();
        lon = weatherEntity.getLon();

        txtCityAndCountryL.setText(String.format("%s, %s", city, country));

        txtLastUpdateL.setText(String.format("%s: %s",
                resources.getString(R.string.last_update),
                lastUpdate));
        txtDescriptionL.setText(String.format("%s", description));
        txtHumidityL.setText(String.format("%s: %d%%",
                resources.getString(R.string.humidity),
                humidity));
        txtTimeL.setText(String.format("%s: %s \n%s: %s",
                resources.getString(R.string.sunrise),
                Common.unixTimeStampToDateTime(sunrise),
                resources.getString(R.string.sunset),
                Common.unixTimeStampToDateTime(sunset)));
        txtCelsiusL.setText(String.format("%s: %.2f °C",
                resources.getString(R.string.temperature),
                temp));

        dialog.dismiss();
    }

    class GetWeather extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isNetworkConnection()){
                txtConnectionL.setText("Check your network connection");
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

            if(s == null){
                dialog.dismiss();
                return;
            }

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            Resources resources = getResources();
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

            txtCityAndCountryL.setText(String.format("%s, %s", city, country));

            txtLastUpdateL.setText(String.format("%s: %s",
                    resources.getString(R.string.last_update),
                    lastUpdate));
            txtDescriptionL.setText(String.format("%s", description));
            txtHumidityL.setText(String.format("%s: %d%%",
                    resources.getString(R.string.humidity),
                    humidity));
            txtTimeL.setText(String.format("%s: %s \n%s: %s",
                    resources.getString(R.string.sunrise),
                    Common.unixTimeStampToDateTime(sunrise),
                    resources.getString(R.string.sunset),
                    Common.unixTimeStampToDateTime(sunset)));
            txtCelsiusL.setText(String.format("%s: %.2f °C",
                    resources.getString(R.string.temperature),
                    temp));
            Picasso.get()
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageViewL);

            country = new CountryCodes().getCountryName(country);

            updateCity = city;
            updateCountry = country;

            WeatherEntity weather = new WeatherEntity(
                    updateWeatherID,
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
            MainActivity.appDatabase.weatherDao().updateWeather(weather);
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.look_weather_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update_look:
                dialog = new AlertDialogComponent(getResources()).setProgressDialog(this);
                new GetWeather().execute(Common.apiRequest(lat, lon));
                break;
            case R.id.delete_look:
                MainActivity.appDatabase.weatherDao().deleteWeatherByID(updateWeatherID);
                Resources resources = getResources();
                Toast.makeText(this, resources.getString(R.string.delete_weather), Toast.LENGTH_SHORT).show();
                Log.i("Delete", "Weather was deleted");
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.show_more_details_look:
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

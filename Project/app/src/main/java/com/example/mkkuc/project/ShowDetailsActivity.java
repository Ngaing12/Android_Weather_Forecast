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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.example.mkkuc.project.common.AlertDialogComponent;
import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.common.CountryCodes;
import com.example.mkkuc.project.common.FixDescription;
import com.example.mkkuc.project.common.FixDoubleValue;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.helper.Helper;
import com.example.mkkuc.project.model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.List;

public class ShowDetailsActivity extends AppCompatActivity {

    int MY_PERMISSION = 0;
    TextView txtConnectionShow, txtCityAndCountryShow, txtLastUpdateShow, txtDescriptionShow,
            txtLatitudeShow, txtLongitudeShow,
            txtPressureShow, txtCelsiusShow, txtTempMinShow, txtTempMaxShow,
            txtWindSpeedShow, txtHumidityShow, txtTimeShow;

    ImageView imageViewShow;
    AlertDialog dialog;
    LocationManager locationManager;
    String provider;
    static double lat, lon;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);
        handleLocation();
    }

    private boolean arePermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShowDetailsActivity.this, new String[]{
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

    private boolean isNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }

    public void handleLocation(){
        dialog = new AlertDialogComponent(getResources()).setProgressDialog(this);
        txtConnectionShow = (TextView) findViewById(R.id.txtConnectionShow);
        txtConnectionShow.setText("");
        txtCityAndCountryShow = (TextView) findViewById(R.id.txtCityAndCountryShow);
        txtLastUpdateShow = (TextView) findViewById(R.id.txtLastUpdateShow);
        txtDescriptionShow = (TextView) findViewById(R.id.txtDescriptionShow);
        txtLatitudeShow = (TextView) findViewById(R.id.txtLatitudeShow);
        txtLongitudeShow = (TextView) findViewById(R.id.txtLongitudeShow);
        txtPressureShow = (TextView) findViewById(R.id.txtPressureShow);
        txtHumidityShow = (TextView) findViewById(R.id.txtHumidityShow);
        txtTimeShow = (TextView) findViewById(R.id.txtTimeShow);
        txtCelsiusShow = (TextView) findViewById(R.id.txtCelsiusShow);
        txtTempMaxShow = (TextView) findViewById(R.id.txtTempMaxShow);
        txtTempMinShow = (TextView) findViewById(R.id.txtTempMinShow);
        txtWindSpeedShow = (TextView) findViewById(R.id.txtWindSpeedShow);
        imageViewShow = (ImageView) findViewById(R.id.imageViewShow);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ShowDetailsActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
            return;
        }
        intent = getIntent();
        FixDoubleValue fix = new FixDoubleValue();
        lat = fix.fixDoubleValue(intent.getStringExtra("lat"));
        lon = fix.fixDoubleValue(intent.getStringExtra("lon"));

        new GetWeather().execute(Common.apiRequest(lat, lon));
    }


    class GetWeather extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isNetworkConnection()){
                txtConnectionShow.setText("Check your network connection");
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
            double pressure = openWeatherMap.getMain().getPressure();
            double temp = openWeatherMap.getMain().getTemp();
            double tempMin = openWeatherMap.getMain().getTemp_min();
            double tempMax = openWeatherMap.getMain().getTemp_max();
            double windSpeed = openWeatherMap.getWind().getSpeed();
            double sunrise = openWeatherMap.getSys().getSunrise();
            double sunset = openWeatherMap.getSys().getSunset();

            txtCityAndCountryShow.setText(String.format("%s, %s", city, country));

            txtLastUpdateShow.setText(String.format("%s: %s",
                    resources.getString(R.string.last_update),
                    lastUpdate));
            txtDescriptionShow.setText(String.format("%s", description));

            txtLatitudeShow.setText(String.format("%s: %.5f",
                    resources.getString(R.string.latitude),
                    lat));

            txtLongitudeShow.setText(String.format("%s: %.5f",
                    resources.getString(R.string.longitude),
                    lon));

            txtPressureShow.setText(String.format("%s: %.2f hPa",
                    resources.getString(R.string.pressure),
                    pressure));

            txtHumidityShow.setText(String.format("%s: %d%%",
                    resources.getString(R.string.humidity),
                    humidity));

            txtCelsiusShow.setText(String.format("%s: %.2f °C",
                    resources.getString(R.string.temperature),
                    temp));

            txtTempMinShow.setText(String.format("%s: %.2f °C",
                    resources.getString(R.string.tempMin),
                    tempMin));

            txtTempMaxShow.setText(String.format("%s: %.2f °C",
                    resources.getString(R.string.tempMax),
                    tempMax));

            txtWindSpeedShow.setText(String.format("%s: %.2f m/s",
                    resources.getString(R.string.wind),
                    windSpeed));

            txtTimeShow.setText(String.format("%s: %s \n%s: %s",
                    resources.getString(R.string.sunrise),
                    Common.unixTimeStampToDateTime(sunrise),
                    resources.getString(R.string.sunset),
                    Common.unixTimeStampToDateTime(sunset)));

            Picasso.get()
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageViewShow);

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
            for(WeatherEntity weatherE : list){
                if(weatherE.getCountry().equals(country)
                        &&
                        weatherE.getCity().equals(city)) {
                    weather.setWeatherID(weatherE.getWeatherID());
                    MainActivity.appDatabase.weatherDao().updateWeather(weather);
                    check = true;
                    break;
                }
            }
            if(!check)
                MainActivity.appDatabase.weatherDao().addWeather(weather);
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.show_weather_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update_show:
                dialog = new AlertDialogComponent(getResources()).setProgressDialog(this);
                new GetWeather().execute(Common.apiRequest(lat, lon));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

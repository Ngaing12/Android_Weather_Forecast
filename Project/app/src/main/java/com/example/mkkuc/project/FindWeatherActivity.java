package com.example.mkkuc.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mkkuc.project.common.Common;
import com.example.mkkuc.project.common.CountryCodes;
import com.example.mkkuc.project.database.WeatherEntity;
import com.example.mkkuc.project.helper.Helper;
import com.example.mkkuc.project.model.Main;
import com.example.mkkuc.project.model.OpenWeatherMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

public class FindWeatherActivity extends AppCompatActivity{

    TextView txtConnectionF, txtCityAndCountryF, txtLastUpdateF, txtDescriptionF, txtHumidityF, txtTimeF, txtCelsiusF;
    ImageView imageViewF;
    AlertDialog dialog;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    String cityFind;
    String countryFind;

    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_weather);
        handleLocation();
    }

    private boolean arePermissions(){
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
        dialog = setProgressDialog();
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

    private AlertDialog setProgressDialog() {

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

    private class GetWeather extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isNetworkConnection()){
                txtConnectionF.setText("Check your network connection");
                dialog.dismiss();
                return;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];

            Helper http = new Helper();
            String stream = http.getHTTPData(urlString);
            if(stream == null){
                txtConnectionF.setText("Something went wrong, try again.");
                return "NULL_VALUE";
            }

            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null || s.equals("NULL_VALUE")){
                dialog.dismiss();
                return;
            }

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
            double lat = openWeatherMap.getCoord().getLat();
            double lon = openWeatherMap.getCoord().getLon();

            txtCityAndCountryF.setText(String.format("%s, %s", city, country));
            txtLastUpdateF.setText(String.format("Last Updated: %s", lastUpdate));
            txtDescriptionF.setText(String.format("%s", description));
            txtHumidityF.setText(String.format("Humidity: %d%%", humidity));
            txtTimeF.setText(String.format("Sunrise: %s \n Sunset: %s",
                    Common.unixTimeStampToDateTime(sunrise),
                    Common.unixTimeStampToDateTime(sunset)));
            txtCelsiusF.setText(String.format("Temperature: %.2f °C", temp));
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


}

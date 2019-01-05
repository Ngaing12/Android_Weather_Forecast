package com.example.mkkuc.project.common;

import android.support.annotation.NonNull;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Common {//f6d8334b80a7514b31b9ad96d4a10d57
    public static String API_KEY = "918d6775e0751eb612962d88e83666ab";
    public static String API_LINK = "http://api.openweathermap.org/data/2.5/weather";

    @NonNull
    public static String apiRequest(double lat, double lon){
        StringBuilder sb = new StringBuilder(API_LINK);
        String _lat = String.valueOf(lat);
        String _lon = String.valueOf(lon);
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric", _lat, _lon, API_KEY));
        return sb.toString();
    }

    @NonNull
    public static String apiRequest(String city, String country){
        StringBuilder sb = new StringBuilder(API_LINK);
        sb.append(String.format("?q=%s,%s&APPID=%s&units=metric", city, country, API_KEY));
        return sb.toString();
    }

    public static String unixTimeStampToDateTime (double unixTimeStamp){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png", icon);
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

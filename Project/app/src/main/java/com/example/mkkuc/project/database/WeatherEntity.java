package com.example.mkkuc.project.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.arch.persistence.room.ColumnInfo;

import com.example.mkkuc.project.adapter.ItemModel;

@Entity(tableName = "weathers")
public class WeatherEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "weatherID")
    private int weatherID;

    @NonNull
    @ColumnInfo(name = "country")
    private String country;

    @NonNull
    @ColumnInfo(name = "city")
    private String city;


    @ColumnInfo(name = "description")
    private String description;


    @ColumnInfo(name = "lastUpdate")
    private String lastUpdate;


    @ColumnInfo(name = "humidity")
    private int humidity;


    @ColumnInfo(name = "lan")
    private double lat;


    @ColumnInfo(name = "lon")
    private double lon;


    @ColumnInfo(name = "temp")
    private double temp;


    @ColumnInfo(name = "sunrise")
    private double sunrise;


    @ColumnInfo(name = "sunset")
    private double sunset;

    public WeatherEntity() {

    }

    public WeatherEntity(String country, String city, String description, String lastUpdate, int humidity, double lat, double lon, double temp, double sunrise, double sunset) {
        this.country = country;
        this.city = city;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.humidity = humidity;
        this.lat = lat;
        this.lon = lon;
        this.temp = temp;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public WeatherEntity(int weatherID, String country, String city, String description, String lastUpdate, int humidity, double lat, double lon, double temp, double sunrise, double sunset) {
        this.weatherID = weatherID;
        this.country = country;
        this.city = city;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.humidity = humidity;
        this.lat = lat;
        this.lon = lon;
        this.temp = temp;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public WeatherEntity(ItemModel itemModel) {
        this.weatherID = itemModel.getWeatherEntity().weatherID;
        this.country = itemModel.getWeatherEntity().getCountry();
        this.city = itemModel.getWeatherEntity().getCity();
        this.description = itemModel.getWeatherEntity().getDescription();
        this.lastUpdate = itemModel.getWeatherEntity().getLastUpdate();
        this.humidity = itemModel.getWeatherEntity().getHumidity();
        this.lat = itemModel.getWeatherEntity().getLat();
        this.lon = itemModel.getWeatherEntity().getLon();
        this.temp = itemModel.getWeatherEntity().temp;
        this.sunrise = itemModel.getWeatherEntity().getSunrise();
        this.sunset = itemModel.getWeatherEntity().getSunset();
    }

    public int getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(int weatherID) {
        this.weatherID = weatherID;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getSunrise() {
        return sunrise;
    }

    public void setSunrise(double sunrise) {
        this.sunrise = sunrise;
    }

    public double getSunset() {
        return sunset;
    }

    public void setSunset(double sunset) {
        this.sunset = sunset;
    }
}

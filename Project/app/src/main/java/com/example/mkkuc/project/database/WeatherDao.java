package com.example.mkkuc.project.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WeatherDao {
    @Insert
    void addWeather(WeatherEntity weather);

    @Query("SELECT * FROM weathers ORDER BY city, country")
    List<WeatherEntity> getWeathers();

    @Query("SELECT city FROM weathers WHERE city LIKE :city AND country LIKE :country ")
    boolean isWeatherByCityAndCountry(String city, String country);

    @Delete
    void deleteWeather(WeatherEntity weather);

    @Update
    void updateWeather(WeatherEntity weather);
}

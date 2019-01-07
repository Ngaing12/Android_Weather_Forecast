package com.example.mkkuc.project.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WeatherDao {
    @Insert
    void addWeather(WeatherEntity weather);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addListOfWeathers(List<WeatherEntity> weather);

    @Query("SELECT * FROM weathers ORDER BY country, city")
    List<WeatherEntity> getWeathers();

    @Query("SELECT * FROM weathers WHERE weatherID = :id")
    WeatherEntity getWeather(int id);

    @Delete
    void deleteWeather(WeatherEntity weather);

    @Query("DELETE FROM weathers")
    void deleteAllWeathers();

    @Query("DELETE FROM weathers WHERE weatherID = :id")
    void deleteWeatherByID(int id);

    @Update
    void updateWeather(WeatherEntity weather);
}

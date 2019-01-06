package com.example.mkkuc.project.adapter;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.mkkuc.project.database.WeatherEntity;

public class ItemModel{
    String name = "";
    boolean checked = false;
    WeatherEntity weatherEntity;

    public ItemModel() { }

    public ItemModel(WeatherEntity weatherEntity) {
        this.name = weatherEntity.getCity() + ", " + weatherEntity.getCountry();
        this.weatherEntity = weatherEntity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public WeatherEntity getWeatherEntity() {
        return weatherEntity;
    }

    public void setWeatherEntity(WeatherEntity weatherEntity) {
        this.weatherEntity = weatherEntity;
    }

    public String toString() {
        return name;
    }

    public void toggleChecked() {
        checked = !checked;
    }

}
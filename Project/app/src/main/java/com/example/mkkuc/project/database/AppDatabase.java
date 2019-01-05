package com.example.mkkuc.project.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {WeatherEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();
}

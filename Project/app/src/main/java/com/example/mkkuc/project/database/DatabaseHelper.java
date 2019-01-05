package com.example.mkkuc.project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weathersHistory";
    private static final String TABLE_WEATHERS = "weathers";
    private static final String KEY_ID = "weatherID";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CITY = "city";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LAST_UPDATE = "lastUpdate";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_TEMP = "temperature";
    private static final String KEY_SUNRISE = "sunrise";
    private static final String KEY_SUNSET = "sunset";

    public DatabaseHelper(Context context){
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WEATHERS_TABLE = "CREATE TABLE " + TABLE_WEATHERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_COUNTRY + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_LAST_UPDATE + " TEXT,"
                + KEY_HUMIDITY + " INTEGER,"
                + KEY_LAT + " REAL,"
                + KEY_LON + " REAL,"
                + KEY_TEMP + " REAL,"
                + KEY_SUNRISE + " REAL,"
                + KEY_SUNSET + " REAL" +")";

        db.execSQL(CREATE_WEATHERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHERS);

        onCreate(db);
    }

    public void addWeather(WeatherEntity weather){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_COUNTRY, weather.getCountry());
        values.put(KEY_CITY, weather.getCity());
        values.put(KEY_DESCRIPTION, weather.getDescription());
        values.put(KEY_LAST_UPDATE, weather.getLastUpdate());
        values.put(KEY_HUMIDITY, weather.getHumidity());
        values.put(KEY_LAT, weather.getLat());
        values.put(KEY_LON, weather.getLon());
        values.put(KEY_TEMP, weather.getTemp());
        values.put(KEY_SUNRISE, weather.getSunrise());
        values.put(KEY_SUNSET, weather.getSunset());

        db.insert(TABLE_WEATHERS, null, values);
        db.close();
    }

    private WeatherEntity getWeather(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WEATHERS, new String[]{
                KEY_ID,
                KEY_COUNTRY,
                KEY_CITY,
                KEY_DESCRIPTION,
                KEY_LAST_UPDATE,
                KEY_HUMIDITY,
                KEY_LAT,
                KEY_LON,
                KEY_TEMP,
                KEY_SUNRISE,
                KEY_SUNSET
        }, KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        int weatherID = Integer.parseInt(cursor.getString(0));
        String country = cursor.getString(1);
        String city = cursor.getString(2);
        String description = cursor.getString(3);
        String lastUpdate = cursor.getString(4);
        int humidity = Integer.parseInt(cursor.getString(5));
        double lat = Double.parseDouble(cursor.getString(6));
        double lon = Double.parseDouble(cursor.getString(7));
        double temp = Double.parseDouble(cursor.getString(8));
        double sunrise = Double.parseDouble(cursor.getString(9));
        double sunset = Double.parseDouble(cursor.getString(10));

        WeatherEntity weather = new WeatherEntity(
                weatherID,
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

        return weather;
    }

    public List<WeatherEntity> getAllWeathers(){
        List<WeatherEntity> weatherList = new ArrayList<>();

        String selectQuery = "SELECT *FROM " + TABLE_WEATHERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                int weatherID = Integer.parseInt(cursor.getString(0));
                String country = cursor.getString(1);
                String city = cursor.getString(2);
                String description = cursor.getString(3);
                String lastUpdate = cursor.getString(4);
                int humidity = Integer.parseInt(cursor.getString(5));
                double lat = Double.parseDouble(cursor.getString(6));
                double lon = Double.parseDouble(cursor.getString(7));
                double temp = Double.parseDouble(cursor.getString(8));
                double sunrise = Double.parseDouble(cursor.getString(9));
                double sunset = Double.parseDouble(cursor.getString(10));

                WeatherEntity weather = new WeatherEntity(
                        weatherID,
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

                weatherList.add(weather);
            }while(cursor.moveToNext());
        }
        return weatherList;
    }

    public void updateWeather(WeatherEntity weather){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNTRY, weather.getCountry());
        values.put(KEY_CITY, weather.getCity());
        values.put(KEY_DESCRIPTION, weather.getDescription());
        values.put(KEY_LAST_UPDATE, weather.getLastUpdate());
        values.put(KEY_HUMIDITY, weather.getHumidity());
        values.put(KEY_LAT, weather.getLat());
        values.put(KEY_LON, weather.getLon());
        values.put(KEY_TEMP, weather.getTemp());
        values.put(KEY_SUNRISE, weather.getSunrise());
        values.put(KEY_SUNSET, weather.getSunset());

        db.update(TABLE_WEATHERS, values, KEY_ID + "=?",
        new String[]{String.valueOf(weather.getWeatherID())});
    }

    public void deleteWeather(WeatherEntity weather){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEATHERS, KEY_ID + "=?",
                new String[]{String.valueOf(weather.getWeatherID())});
        db.close();
    }

    public boolean isWeatherByCityAndCountry(String city, String country){
        String findQuery = "SELECT city FROM " + TABLE_WEATHERS +
                " WHERE city LIKE ? AND country LIKE ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        String[] params = new String[]{city, country};
        Cursor cursor = db.rawQuery(findQuery, params);

        if(!cursor.moveToFirst()){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }
}

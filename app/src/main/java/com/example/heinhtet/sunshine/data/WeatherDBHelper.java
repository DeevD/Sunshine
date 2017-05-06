package com.example.heinhtet.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.heinhtet.sunshine.data.WeatherContract.WeatherEntry;
/**
 * Created by heinhtet on 4/24/17.
 */

public class WeatherDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "weather.db";
    public static final int DB_VERSION = 1;

    public WeatherDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_WEAHTER_TABLE = " CREATE TABLE " + WeatherEntry.TABLE_NAME + " ( "


                + WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                + WeatherEntry.COLUMN_DATE + " INTEGER, "

                + WeatherEntry.COLUMN_WEATHER_ID + " INTEGER, "

                + WeatherEntry.COLUMN_MAX_TEMP + " REAL, "

                + WeatherEntry.COLUMN_MIN_TEMP + " REAL, "

                + WeatherEntry.COLUMN_PRESSURE + " REAL, "

                + WeatherEntry.COLUMN_HUMIDITY + " REAL, "

                + WeatherEntry.COLUMN_WIND_SPEED + " REAL, "

                + WeatherEntry.COLUMN_DEGREE + " REAL); ";


        db.execSQL(SQL_WEAHTER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+ WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}

package com.example.heinhtet.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.heinhtet.sunshine.utilities.SunshineDateUtils;

import static com.example.heinhtet.sunshine.data.WeatherContract.WeatherEntry.CONTENT_URL;

/**
 * Created by heinhtet on 4/24/17.
 */

public class WeatherContract  {

    public static final String Authority = "com.example.heinhtet.sunshine";
    public static final String PATH_WATHER = "weather";
    public static final Uri BASE_CONTENT_URL = Uri.parse("content://"+Authority);

    public static class WeatherEntry implements BaseColumns{
        public static final Uri CONTENT_URL = BASE_CONTENT_URL.buildUpon().appendPath(PATH_WATHER).build();

        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREE = "degrees";


        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URL.buildUpon().appendPath(Long.toString(date)).build();
        }
    }


    /**
     * Returns just the selection part of the weather query from a normalized today value.
     * This is used to get a weather forecast from today's date. To make this easy to use
     * in compound selection, we embed today's date as an argument in the query.
     *
     * @return The selection part of the weather query for today onwards
     */
    public static String getSqlSelectForTodayOnwards() {
        long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
        return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
    }
    public static  Uri buildWeatherUriWithDate(long date) {
        return CONTENT_URL.buildUpon()
                .appendPath(Long.toString(date))
                .build();
    }
}

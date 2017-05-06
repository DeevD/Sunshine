package com.example.heinhtet.sunshine;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.heinhtet.sunshine.data.SunshinePreferences;
import com.example.heinhtet.sunshine.data.WeatherContract;
import com.example.heinhtet.sunshine.sync.SunshineSyncUtils;
import com.example.heinhtet.sunshine.utilities.NetworkUtils;
import com.example.heinhtet.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, android.app.LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    TextView weather_tv;
    TextView error_tv;
    ProgressBar progressBar;
    RecyclerView recyclerview_forecast_rv;
    ForecastAdapter forecastListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView location_text;
    Boolean PREFERENCE_HAVE_BEEN_UPDATE = false;

    final int LOADER_ID = 1;

    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    int mPosition = recyclerview_forecast_rv.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
//        linearLayout.setBackgroundColor(Color.parseColor(color));

        location_text = (TextView) findViewById(R.id.location);
        location_text.setText("Fetch Weather Location -" + SunshinePreferences.getPreferredWeatherLocation(this));

        error_tv = (TextView) findViewById(R.id.error_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_id);
        recyclerview_forecast_rv = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        recyclerview_forecast_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview_forecast_rv.setHasFixedSize(true);

        forecastListAdapter = new ForecastAdapter(this, this);
        recyclerview_forecast_rv.setAdapter(forecastListAdapter);


        Bundle loaderBundle = null;
        getLoaderManager().initLoader(LOADER_ID, null, MainActivity.this);

        showLoading();
        SunshineSyncUtils.startImmediateSync(this);


//        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            //loadWeatherData();
//            getLoaderManager().initLoader(LOADER_ID,loaderBundle,MainActivity.this);
//            Log.v(TAG,"Start Loader is call");
//        } else {
//            showErrorMessage();
//            if (progressBar.isShown()) {
//                progressBar.setVisibility(View.VISIBLE);
//            }
//            error_tv.setText("No Internet Connnection");
//        }
        swipeRefreshLayoutCalling();


        setUpSharePreference();

    }

    public void setUpSharePreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadColorFromPreference();
        shownColor();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

    }


    private void showLoading() {
        recyclerview_forecast_rv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void openMapLocation() {
        String address_location = "1600 Ampitheatre Parkway, CA";
        String prefer_location = SunshinePreferences.getPreferredWeatherLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?q=" + prefer_location);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }

    }

    public void showWeatherDataView() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerview_forecast_rv.setVisibility(View.VISIBLE);

    }

    public void showErrorMessage() {
        error_tv.setVisibility(View.VISIBLE);
        recyclerview_forecast_rv.setVisibility(View.VISIBLE);

    }

    public void loadWeatherData() {
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        Log.v(MainActivity.class.getSimpleName(), "Lik" + location);
        new FetchWeatherTask().execute(location);
    }

//    @Override
//    public Loader<String[]> onCreateLoader(int id, Bundle args) {
//
//        return new AsyncTaskLoader<String[]>(this) {
//
//            String[] mWeatherData = null;
//            @Override
//            protected void onStartLoading() {
//
//                if (mWeatherData != null) {
//                    progressBar.setVisibility(View.GONE);
//                    showWeatherDataView();
//                    deliverResult(mWeatherData);
//
//                } else {
//                    progressBar.setVisibility(View.VISIBLE);
//                    forceLoad();
//                }
//            }
//
//            @Override
//            public void deliverResult(String[] data) {
//                mWeatherData= data;
//                super.deliverResult(data);
//            }
//
//
//
//            @Override
//            public String[] loadInBackground() {
//                String location = SunshinePreferences.getPreferredWeatherLocation(getContext());
//                Log.d("Location " , location);
//                URL weatherURL = NetworkUtils.getUrl(MainActivity.this);
//
//                try
//                {
//                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(weatherURL);
//                    String [] data = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,jsonResponse);
//                    return data;
//
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                    return null;
//                }
//
//            }
//            /*
//            For cache , when load is finish
//            cache is avalible whein finish loading
//             */
//
//
//        };
//    }
//
//    @Override
//    public void onLoadFinished(Loader<String[]> loader, String[] data) {
//        if (data!= null)
//        {
//            progressBar.setVisibility(View.INVISIBLE);
//            Log.v(TAG,"Finish Loader");
//            showWeatherDataView();
//            setForecastListAdapter(data);
//
//        }else {
//            showErrorMessage();
//        }
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<String[]> loader) {
//
//    }

//    @Override
//    public void onClick(String weatherForDay) {
//        Context context = this;
//        Intent i = new Intent(context, DetailActivity.class);
//        i.putExtra(Intent.EXTRA_TEXT, weatherForDay);
//        startActivity(i);
//        Toast.makeText(context, "This weather " + weatherForDay, Toast.LENGTH_SHORT)
//                .show();
//
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCE_HAVE_BEEN_UPDATE = true;
        if (key.equals((getString(R.string.pref_color_key)))) {
            loadColorFromPreference();

        } else if (key.equals(getString(R.string.bool))) {

            shownColor();
        }
        else if (key.equals(getString(R.string.language_mm)))
        {

        }

    }

    private void shownColor() {
        boolean aBoolean = SunshinePreferences.isShown(this);
        if (aBoolean) {
            Log.d("showncolor", "::" + aBoolean);
            recyclerview_forecast_rv.setBackgroundColor(ContextCompat.getColor(this, R.color.cardview_shadow_start_color));
            location_text.setText("True");
        }
    }

    public void loadColorFromPreference() {
        String color = SunshinePreferences.getPreferredBackgroundColor(this);
        int colorId = setColor(color);
        recyclerview_forecast_rv.setBackgroundColor(colorId);
    }

    public int setColor(String newColorKey) {

        @ColorInt
        int shapeColor;

        @ColorInt
        int trailColor;

        int backgroundColor;
        Log.d("Color Tag", "::" + newColorKey);
        if (newColorKey.equals(getString(R.string.pref_color_red_value))) {
            shapeColor = ContextCompat.getColor(this, R.color.swipeColor2);
            trailColor = ContextCompat.getColor(this, R.color.swipeColor3);
            backgroundColor = ContextCompat.getColor(this, R.color.swipeColor3);
        } else if (newColorKey.equals(this.getString(R.string.pref_color_blue_value))) {
            shapeColor = ContextCompat.getColor(this, R.color.cardview_light_background);
            trailColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            backgroundColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        } else {
            shapeColor = ContextCompat.getColor(this, R.color.cardview_shadow_end_color);
            trailColor = ContextCompat.getColor(this, R.color.colorPrimary);
            backgroundColor = ContextCompat.getColor(this, R.color.swipeColor2);
        }
        return backgroundColor;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCE_HAVE_BEEN_UPDATE) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            location_text.setText("Fetch Weather Location -" + SunshinePreferences.getPreferredWeatherLocation(this));
            loadColorFromPreference();
            shownColor();
            PREFERENCE_HAVE_BEEN_UPDATE = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int Loader_id, Bundle args) {
        switch (Loader_id) {

            case LOADER_ID:
                /* URI for all rows of weather data in our weather table */
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URL;
                /* Sort order: Ascending by date */
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                String selection = WeatherContract.getSqlSelectForTodayOnwards();
                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + Loader_id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        forecastListAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        recyclerview_forecast_rv.smoothScrollToPosition(mPosition);

        showWeatherDataView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastListAdapter.swapCursor(null);
    }

    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
    }


    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {


            if (params == null) {
                return null;
            }

            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.getUrl(MainActivity.this);


            try {
                //getting JsonResponse
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                Log.v(MainActivity.class.getSimpleName(), "jsonResponse HTTp" + jsonWeatherResponse);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String[] strings) {
            if (progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            if (strings != null) {
                for (String str : strings) {
//                    setForecastListAdapter(strings);

                }
            } else {
                showErrorMessage();
            }

            super.onPostExecute(strings);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh: {
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                Log.v(TAG, "Resert Loader");
                // loadWeatherData();
                break;
            }

            case R.id.action_map: {
                openMapLocation();
                break;
            }
            case R.id.action_settings: {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }


//    private void setForecastListAdapter(String[]data)
//    {
//        forecastListAdapter.setWeatherData(data);
//    }
//
//    private void invlidSetData()
//    {
//        forecastListAdapter.setWeatherData(null);
//    }

    private void swipeRefreshLayoutCalling() {

          /*
        For Running time for swipeRefreshLayout that is now 3 seconds
         */
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {

                            getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                        }

                    }
                }, 3000);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.swipeColor2, R.color.swipeColor3);
        swipeRefreshLayout.setSize(400);

    }
}

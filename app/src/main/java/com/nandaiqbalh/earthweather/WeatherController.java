package com.nandaiqbalh.earthweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nandaiqbalh.earthweather.model.WeatherData;

import org.json.JSONObject;

import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class WeatherController extends AppCompatActivity {

    // Declare Constant
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "0eab0d218a11b964b2c23bf96f0fb714";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;
    // LogCat tag
    final String LOGCAT_TAG = "EW";

    // Set Location Provider
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Location Manager(Request or Stop) and Location Listener(Memonitor jika ada perubahan)
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    ImageView ivWeatherIcon;
    TextView tvLocationName;
    TextView tvCountry;
    TextView tvDate;
    TextView tvTemperature;
    TextView tvWeatherName;
    TextView tvWind, tvPressure, tvHumidity, tvVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_controller);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inisialisasi();

    }


    @Override
    protected void onResume() {
        super.onResume();

        getWeatherForCurrentLocation();

    }

    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                Log.d(LOGCAT_TAG, "OnLocationChanged() called");

                // menambahkan String, untuk mendapatkan longitude dan latitude
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                // tampilkan longitude dan latitude ke LogCat
                Log.d(LOGCAT_TAG, "The longitude is : " + longitude);
                Log.d(LOGCAT_TAG, "The latitude is : " + latitude);

                // membuat params untuk disuplaikan ke API dan networking
                RequestParams params = new RequestParams(); // RequestParams ini dari library loopj.async
                // suplay params dengan params.put
                params.put("lon", longitude); // dalam kurung (key, value)
                params.put("lat", latitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

                Log.d(LOGCAT_TAG, "onProviderDisabled() called");

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGCAT_TAG, "Permission Granted!");
                // jika diizinkan dan true maka akan panggil method di bawah, dan masuk method onLocationChanged.
                getWeatherForCurrentLocation();
            }
            // jika tidak dapat izin maka permission denied, dan akan muncul Toast message
        } else {
            Log.d(LOGCAT_TAG, "Permission Denied!");
            Toast.makeText(getApplicationContext(), "Failed! location access denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient(); // dari james library (loopj)
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){ // (URL, Params, Output)

            // buat method apabila request kita sukses
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                Log.d(LOGCAT_TAG, "Succes! JSON : " + response.toString());

                WeatherData weatherDataModel = WeatherData.fromJson(response);
                updateUI(weatherDataModel);


            }

            // method jika request kita gagal
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e(LOGCAT_TAG, "Error :" + e.toString());
                Log.d(LOGCAT_TAG, "onFailure: status code" + statusCode);
                Toast.makeText(WeatherController.this, "Request Failed!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // update UI
    private void updateUI(WeatherData weatherData){

        // icon
        int resourceID = getResources().getIdentifier(weatherData.getmIconName(), "drawable", getPackageName());
        ivWeatherIcon.setImageResource(resourceID);

        // location name
        tvLocationName.setText(weatherData.getTvLocationName());

        // location name
        tvCountry.setText(", " + weatherData.getTvCountry());
        tvCountry.setVisibility(View.VISIBLE);

        // time
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        tvDate.setText(currentDateTimeString);

        // temperature
        tvTemperature.setText(weatherData.getTvTemperature());

        // weather name
        tvWeatherName.setText(weatherData.getTvWeatherName());

        // wind
        tvWind.setText(weatherData.getTvWind() + " km/h");

        // pressure
        tvPressure.setText(weatherData.getTvPressure() + " mbar");

        // humidity
        tvHumidity.setText(weatherData.getTvHumidity() + " %");

        // visibility
        tvVisibility.setText(weatherData.getTvVisibility() + " m");
    }

    private void inisialisasi(){
        ivWeatherIcon = (ImageView) findViewById(R.id.iv_icon_weather);

        tvLocationName = (TextView) findViewById(R.id.tv_location);
        tvCountry = (TextView)findViewById(R.id.tv_country);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        tvWeatherName = (TextView) findViewById(R.id.tv_weather_name);
        tvWind = (TextView) findViewById(R.id.tv_wind);
        tvPressure = (TextView) findViewById(R.id.tv_pressure);
        tvHumidity = (TextView) findViewById(R.id.tv_humidity);
        tvVisibility = (TextView) findViewById(R.id.tv_visibility);

    }
}
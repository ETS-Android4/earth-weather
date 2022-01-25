package com.nandaiqbalh.earthweather.model;

import android.content.Intent;
import android.util.JsonReader;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    // TODO: Declare the member variables here

    private String tvLocationName;
    private String tvTemperature;
    private int mCondition;
    private String mIconName;
    private String tvWeatherName;
    private String tvWind, tvPressure, tvHumidity, tvVisibility;

    // TODO: Create a WeatherDataModel from a JSON:
    public static WeatherData fromJson(JSONObject jsonObject) {
        try {

            WeatherData weatherData = new WeatherData();

            weatherData.tvLocationName = jsonObject.getString("name");
            weatherData.tvWeatherName = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            weatherData.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mIconName = updateWeatherIcon(weatherData.mCondition);

            double dbWind = jsonObject.getJSONObject("wind").getDouble("speed");
            int rdWind = (int) Math.rint(dbWind);
            weatherData.tvWind = Integer.toString(rdWind);

            double dbPressure = jsonObject.getJSONObject("main").getDouble("pressure");
            int rdPressure = (int) Math.rint(dbPressure);
            weatherData.tvPressure = Integer.toString(rdPressure);

            double dbHumidity = jsonObject.getJSONObject("main").getDouble("humidity");
            int rdHumidity = (int) Math.rint(dbHumidity);
            weatherData.tvHumidity = Integer.toString(rdHumidity);

            double dbVisibility = jsonObject.getDouble("visibility");
            int rdVisibility = (int) Math.rint(dbVisibility);
            weatherData.tvVisibility = Integer.toString(rdVisibility);

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(tempResult);
            weatherData.tvTemperature = Integer.toString(roundedValue);

            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    // TODO: Uncomment to this to get the weather image name from the condition:
    private static String updateWeatherIcon(int condition) {
        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    public String getTvLocationName() {
        return tvLocationName;
    }

    public String getTvTemperature() {
        return tvTemperature + "°";
    }

    public int getmCondition() {
        return mCondition;
    }

    public String getmIconName() {
        return mIconName;
    }

    public String getTvWeatherName() {
        return tvWeatherName;
    }

    public String getTvWind() {
        return tvWind;
    }

    public String getTvPressure() {
        return tvPressure;
    }

    public String getTvHumidity() {
        return tvHumidity;
    }

    public String getTvVisibility() {
        return tvVisibility;
    }
}

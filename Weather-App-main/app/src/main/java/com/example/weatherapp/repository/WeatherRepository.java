package com.example.weatherapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherapp.model.WeatherResponse;
import com.example.weatherapp.network.RetrofitClient;
import com.example.weatherapp.network.WeatherService;
import com.example.weatherapp.util.Resource;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private static final String PREFS_NAME = "weather_prefs";
    private static final String KEY_LAST_CITY = "last_city";
    private static final String KEY_LAST_WEATHER_JSON = "last_weather_json";

    private final WeatherService weatherService;
    private final SharedPreferences prefs;
    private final Gson gson;

    public WeatherRepository(Context context) {
        weatherService = RetrofitClient.getWeatherService();
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public LiveData<Resource<WeatherResponse>> fetchCurrentWeather(String city, String apiKey, String units) {
        MutableLiveData<Resource<WeatherResponse>> result = new MutableLiveData<>();
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(apiKey)) {
            result.setValue(Resource.error("City or API key is empty", null));
            return result;
        }

        result.setValue(Resource.loading(null));
        weatherService.getCurrentWeather(city, apiKey, units).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse body = response.body();
                    // Save last successful result
                    saveLastCity(city);
                    saveLastWeatherJson(gson.toJson(body));
                    result.postValue(Resource.success(body));
                } else {
                    // Try to extract error message
                    String message = "API error: " + response.code();
                    result.postValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                result.postValue(Resource.error("Network failure: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public void saveLastCity(String city) {
        prefs.edit().putString(KEY_LAST_CITY, city).apply();
    }

    public String getLastCity() {
        return prefs.getString(KEY_LAST_CITY, "");
    }

    public void saveLastWeatherJson(String json) {
        prefs.edit().putString(KEY_LAST_WEATHER_JSON, json).apply();
    }

    public WeatherResponse getLastWeatherFromCache() {
        String json = prefs.getString(KEY_LAST_WEATHER_JSON, "");
        if (json == null || json.isEmpty()) return null;
        try {
            return gson.fromJson(json, WeatherResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}

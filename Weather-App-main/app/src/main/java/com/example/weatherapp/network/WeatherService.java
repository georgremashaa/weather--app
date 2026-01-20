package com.example.weatherapp.network;

import com.example.weatherapp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    // Current weather data
    // Example: https://api.openweathermap.org/data/2.5/weather?q=London&appid=YOUR_KEY&units=metric
    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units // "metric" or "imperial"
    );
}

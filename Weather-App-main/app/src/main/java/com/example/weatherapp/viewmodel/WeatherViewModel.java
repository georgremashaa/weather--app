package com.example.weatherapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherapp.model.WeatherResponse;
import com.example.weatherapp.repository.WeatherRepository;
import com.example.weatherapp.util.Resource;

public class WeatherViewModel extends AndroidViewModel {

    private final WeatherRepository repository;
    private final MutableLiveData<Resource<WeatherResponse>> weatherLiveData = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        repository = new WeatherRepository(application.getApplicationContext());
    }

    public LiveData<Resource<WeatherResponse>> getWeatherLiveData() {
        return weatherLiveData;
    }

    /**
     * Fetch weather for a city. Units should be "metric" or "imperial".
     * @param city city name
     * @param apiKey API key string
     * @param units units
     */
    public void fetchWeather(String city, String apiKey, String units) {
        LiveData<Resource<WeatherResponse>> source = repository.fetchCurrentWeather(city, apiKey, units);
        // Observe single source and forward values
        source.observeForever(resource -> weatherLiveData.postValue(resource));
    }

    public String getLastCachedCity() {
        return repository.getLastCity();
    }

    public WeatherResponse getLastCachedWeather() {
        return repository.getLastWeatherFromCache();
    }
}

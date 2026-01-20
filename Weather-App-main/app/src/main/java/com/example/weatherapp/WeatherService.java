package com.example.weatherapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {

    public interface Callback {
        void onSuccess(Weather weather);
        void onError(Exception e);
    }

    private static final OkHttpClient client = new OkHttpClient();

    // Uses OpenWeatherMap's Current Weather Data API (example). API key must be provided via strings.xml.
    public static void fetchCurrentWeather(Context context, String city, String apiKey, final Callback callback) {
        // IMPORTANT: Do not hard-code API keys in source. The key must be placed in strings.xml (or safer storage).
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("REPLACE_WITH_YOUR_API_KEY")) {
            new Handler(Looper.getMainLooper()).post(() -> callback.onError(new IllegalStateException("API key is missing. Replace REPLACE_WITH_YOUR_API_KEY in strings.xml with your API key.")));
            return;
        }

        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s", city, apiKey);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(new IOException("Unexpected code " + response)));
                    return;
                }

                String body = response.body() != null ? response.body().string() : null;
                try {
                    Weather weather = parseWeather(body);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(weather));
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }

    private static Weather parseWeather(String json) throws JSONException {
        if (json == null) return null;
        JSONObject obj = new JSONObject(json);
        JSONObject main = obj.optJSONObject("main");
        JSONArray weatherArr = obj.optJSONArray("weather");
        String name = obj.optString("name");

        double temp = main != null ? main.optDouble("temp", Double.NaN) : Double.NaN;
        String description = null;
        if (weatherArr != null && weatherArr.length() > 0) {
            JSONObject w = weatherArr.optJSONObject(0);
            if (w != null) description = w.optString("description");
        }

        Weather weather = new Weather();
        weather.name = name;
        weather.tempCelsius = temp;
        weather.description = description != null ? description : "";
        return weather;
    }
}

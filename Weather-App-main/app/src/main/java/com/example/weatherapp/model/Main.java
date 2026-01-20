package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    public double temp;

    @SerializedName("feels_like")
    public double feels_like;

    @SerializedName("temp_min")
    public double temp_min;

    @SerializedName("temp_max")
    public double temp_max;

    @SerializedName("pressure")
    public int pressure;

    @SerializedName("humidity")
    public int humidity;
}

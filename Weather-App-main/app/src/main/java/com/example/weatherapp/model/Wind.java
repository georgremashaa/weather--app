package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    public double speed;

    @SerializedName("deg")
    public int deg;
}

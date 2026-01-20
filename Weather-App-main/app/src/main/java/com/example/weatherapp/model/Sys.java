package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class Sys {
    @SerializedName("type")
    public int type;

    @SerializedName("id")
    public int id;

    @SerializedName("country")
    public String country;

    @SerializedName("sunrise")
    public long sunrise;

    @SerializedName("sunset")
    public long sunset;
}

package com.example.go4lunchjava.places_api.pojo.details.hours;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.type.TimeOfDay;

public class Close {

    @SerializedName("day")
    @Expose
    private int day;

    @SerializedName("time")
    @Expose
    private String timeOfDay;

    public int getDay() {
        return day;
    }

    public String getTime() {
        return timeOfDay;
    }
}

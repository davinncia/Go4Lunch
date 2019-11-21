package com.example.go4lunchjava.places_api.pojo.details.hours;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHoursDetails {

    @SerializedName("periods")
    @Expose
    private OpeningPeriod[] periods;

    public OpeningPeriod[] getPeriods() {
        return periods;
    }
}

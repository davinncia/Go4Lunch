package com.example.go4lunchjava.places_api.pojo.details.hours;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningPeriod {

    @SerializedName("open")
    @Expose
    private Open open;

    @SerializedName("close")
    @Expose
    private Close close;

    //Constructor (for Testing)
    public OpeningPeriod(Open open, Close close) {
        this.open = open;
        this.close = close;
    }

    public Open getOpen() {
        return open;
    }

    public Close getClose() {
        return close;
    }
}

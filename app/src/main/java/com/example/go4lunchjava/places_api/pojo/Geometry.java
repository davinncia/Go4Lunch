package com.example.go4lunchjava.places_api.pojo;

import androidx.annotation.VisibleForTesting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    @VisibleForTesting
    public Geometry(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}

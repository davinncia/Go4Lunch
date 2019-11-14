package com.example.go4lunchjava.map;

import androidx.annotation.DrawableRes;

import com.example.go4lunchjava.R;

public class Poi {

    private String mName;
    private String mId;
    private Double lat;
    private Double lon;
    @DrawableRes
    private int pointerRes = R.drawable.ic_pointer_red;

    public Poi(String name, String id, Double lat, Double lon) {
        this.mName = name;
        this.mId = id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getId() {
        return mId;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public int getPointerRes() {
        return pointerRes;
    }

    public void setPointerRes(int resource) {
        this.pointerRes = resource;
    }
}

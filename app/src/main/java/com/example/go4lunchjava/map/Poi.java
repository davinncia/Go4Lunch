package com.example.go4lunchjava.map;

public class Poi {

    private String Name;
    private Double lat;
    private Double lon;

    public Poi(String name, Double lat, Double lon) {
        Name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}

package com.example.go4lunchjava.utils;


import android.location.Location;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.places_api.pojo.Geometry;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class RestaurantDataFormat {

    /**
     * Util method that converts rating on three stars and returns the appropriate image resource
     * @param rate Initial rate on five
     * @return int ImageResource
     */

    public static int getRatingResource(Float rate){

        if (rate == null) return 0; //This won't display any image

        //TODO TEST: Unit test for rating
        int result = Math.round(rate * 3 / 5);

        if (result < 0)
            return -1; //TEST
        else if (result < 1.5)
            return R.drawable.ic_star;
        else if (result >= 1.5 && result <= 2.5)
            return R.drawable.ic_star_two;
        else return R.drawable.ic_star_three;

    }

    /**
     * Util method that provide the map API uri to a picture
     * @param pictureReference
     * @return PlacesAPI uri of picture in String
     */
    public static String getPictureUri(String pictureReference){
        String uri = "";
        if (pictureReference == null || pictureReference.isEmpty()) return uri;

        uri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4"
                + "&photoreference="
                + pictureReference;

        return uri;
    }

    /**
     * Util method calculating the distance between two LatLng objects
     * @param latLng1
     * @param geometry
     * @return formatted String of the distance
     */
    public static String getDistanceFromRestaurant(LatLng latLng1, Geometry geometry){

        float[] distanceResult = new float[1];
        String distanceString = "";


        if (latLng1 != null) {
            Location.distanceBetween(
                    geometry.getLocation().getLat(), geometry.getLocation().getLng(),
                    latLng1.latitude, latLng1.longitude,
                    distanceResult);

            if (distanceResult[0] < 1000){
                distanceString = Math.round(distanceResult[0]) + "m";
            } else if (distanceResult[0] >= 1000 && distanceResult[0] < 100000){
                distanceResult[0] /= 1000;
                distanceString = String.format(Locale.ENGLISH, "%.1f", distanceResult[0]) + "km";
            } else if (distanceResult[0] >= 100000){
                distanceString = ">100km";
            }
        }
        return distanceString;
    }
}

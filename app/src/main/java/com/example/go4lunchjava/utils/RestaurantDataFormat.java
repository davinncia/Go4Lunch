package com.example.go4lunchjava.utils;

import android.util.Log;

import com.example.go4lunchjava.R;

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
}

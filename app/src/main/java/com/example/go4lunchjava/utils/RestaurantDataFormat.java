package com.example.go4lunchjava.utils;


import android.location.Location;
import android.util.Log;

import com.example.go4lunchjava.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
            return 0; //No rating communicated
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
     * @param latLng1 of current place
     * @param latLng2 of restaurant
     * @return formatted String of the distance
     */
    public static String getDistanceFromRestaurant(LatLng latLng1, LatLng latLng2){

        float[] distanceResult = new float[1];
        String distanceString = "";


        if (latLng1 != null) {
            Location.distanceBetween(
                    latLng2.latitude, latLng2.longitude,
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

    /**
     * Util method formatting the hour displayed on screen
     *
     * @param openingHours form Places API or SDK
     * @return String
     */

    public static String getHoursFromOpeningHours(OpeningHours openingHours){
        if (openingHours == null){
            return "Opening hours not communicated";
        }

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int dayInt = calendar.get(Calendar.DAY_OF_WEEK); //6
        String day = "";

        switch (dayInt) {
            case 1:
                day = "SUNDAY";
                break;
            case 2:
                day = "MONDAY";
                break;
            case 3:
                day = "TUESDAY";
                break;
            case 4:
                day = "WEDNESDAY";
                break;
            case 5:
                day = "THURSDAY";
                break;
            case 6:
                day = "FRIDAY";
                break;
            case 7:
                day = "SATURDAY";
                break;
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);


        String hours = "Closed";

        for (Period period : openingHours.getPeriods()) {

            if (period.getOpen() != null && period.getOpen().getDay().toString().equals(day)) {
                //Open today !
                if (period.getClose() == null) {
                    hours = "24/7";
                } else {

                    int minutesStillOpen = 0;

                    if (hour < period.getClose().getTime().getHours()) {
                        //Still open
                        minutesStillOpen = 60 * (period.getClose().getTime().getHours() - hour)
                                + period.getClose().getTime().getMinutes() - min;
                    } else if (hour == period.getClose().getTime().getHours() && min <= period.getClose().getTime().getMinutes()) {
                        //Soon closed
                        minutesStillOpen = period.getClose().getTime().getMinutes() - min;
                    }

                    if (minutesStillOpen > 30) {
                        hours = "Open until " + period.getClose().getTime().getHours() + "h" + period.getClose().getTime().getMinutes();
                        try {
                            //Convert to 12 hours format
                            String _24HourTime = period.getClose().getTime().getHours() + ":" + period.getClose().getTime().getMinutes();
                            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                            Date _24HourDt = _24HourSDF.parse(_24HourTime);

                            assert _24HourDt != null;
                            hours = "Open until " + _12HourSDF.format(_24HourDt);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else if (minutesStillOpen > 0) {
                        hours = "Closing soon";
                    } else {
                        hours = "Closed";
                    }

                }
            }
        }
        return hours;
    }
}

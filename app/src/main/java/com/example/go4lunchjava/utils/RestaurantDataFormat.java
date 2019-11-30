package com.example.go4lunchjava.utils;


import android.location.Location;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningHoursDetails;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningPeriod;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RestaurantDataFormat {

    /**
     * Util method that converts rating on three stars and returns the appropriate image resource
     *
     * @param rate Initial rate on five
     * @return int ImageResource
     */

    public static int getRatingResource(Float rate) {

        if (rate == null) return 0; //This won't display any image

        int result = Math.round(rate * 3 / 5);

        if (result <= 0) //Minimum is 1
            return 0; //No rating communicated
        else if (result < 1.5)
            return R.drawable.ic_star;
        else if (result >= 1.5 && result <= 2.5)
            return R.drawable.ic_star_two;
        else return R.drawable.ic_star_three;

    }

    /**
     * Util method that provide the map API uri to a picture
     *
     * @param pictureReference
     * @return PlacesAPI uri of picture in String
     */
    public static String getPictureUri(String pictureReference) {
        String uri = "";
        if (pictureReference == null || pictureReference.isEmpty()) return uri;

        uri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4"
                + "&photoreference="
                + pictureReference;

        return uri;
    }

    /**
     * Util method calculating the distance between two LatLng objects
     *
     * @param latLng1 of current place
     * @param latLng2 of restaurant
     * @return formatted String of the distance
     */
    public static String getDistanceFromRestaurant(LatLng latLng1, LatLng latLng2) {

        float[] distanceResult = new float[1];
        String distanceString = "";


        if (latLng1 != null) {
            Location.distanceBetween(
                    latLng2.latitude, latLng2.longitude,
                    latLng1.latitude, latLng1.longitude,
                    distanceResult);

            if (distanceResult[0] < 1000) {
                distanceString = Math.round(distanceResult[0]) + "m";
            } else if (distanceResult[0] >= 1000 && distanceResult[0] < 100000) {
                distanceResult[0] /= 1000;
                distanceString = String.format(Locale.ENGLISH, "%.1f", distanceResult[0]) + "km";
            } else if (distanceResult[0] >= 100000) {
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
    //TODO: we should use resource string...
    public static String getHoursFromOpeningHours(OpeningHoursDetails openingHours, Calendar calendar) {
        if (openingHours == null) {
            return "Opening hours not communicated";
        }

        //Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int dayInt = calendar.get(Calendar.DAY_OF_WEEK) - 1; //Off by one with PlacesApi

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);


        String hours = "Closed today";

        for (OpeningPeriod period : openingHours.getPeriods()) {

            if (period.getOpen() != null && period.getOpen().getDay() == dayInt && !period.getOpen().getTime().isEmpty()) {
                //Open today !
                if (period.getClose() == null) {
                    return "24/7";
                }

                int openingHour = Integer.parseInt(period.getOpen().getTime().substring(0, 2));
                int openingMinutes = Integer.parseInt(period.getOpen().getTime().substring(2));

                int closingHour = Integer.parseInt(period.getClose().getTime().substring(0, 2));
                if (closingHour < 5) closingHour = 24;
                int closingMinutes = Integer.parseInt(period.getClose().getTime().substring(2));


                if (hour < closingHour && hour >= openingHour || hour == closingHour && min < closingMinutes) {
                    //OPEN NOW
                    int minutesStillOpen = 60 * (closingHour - hour) + closingMinutes - min;

                    if (minutesStillOpen > 30) {
                        hours = "Open until " + convertTo12HoursFormat(closingHour, closingMinutes);

                    } else if (minutesStillOpen > 0) {
                        hours = "Closing soon";
                    }

                } else if (hour < openingHour || hour == openingHour && min < openingMinutes){
                    //BEFORE OPENING
                    hours = "Closed until " + convertTo12HoursFormat(openingHour, openingMinutes);
                }
            }

        }
        return hours;
    }

    private static String convertTo12HoursFormat(int hours, int minutes){
        String hour12 = "";
        try {
            //Convert to 12 hours format
            String _24HourTime = hours + ":" + minutes;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);

            assert _24HourDt != null;
            hour12 = _12HourSDF.format(_24HourDt);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hour12;
    }
}

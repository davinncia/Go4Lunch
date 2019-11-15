package com.example.go4lunchjava.utils;

import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.restaurant_list.RestaurantItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Util Class that converts objects returned by the PlacesApi into UI Objets
 */
public class ObjectConverter {

    public static List<RestaurantItem> convertNearbyResponseToRestaurantItemList(NearBySearchResponse response, LatLng currentLatLng) {

        if (response == null) return null;

        List<RestaurantItem> restaurants = new ArrayList<>();
        for (NearBySearchResult result : response.getResults()) {
            //NAME
            String name = result.getName();

            //ID
            String placeId = result.getPlaceId();

            //ADDRESS
            String address = result.getVicinity();

            //OPENING HOURS
            String hours = "Opening hours not communicated";
            if (result.getOpeningHours() != null) {
                if (result.getOpeningHours().getOpenNow()) hours = "Open now";
                else hours = "Closed";
            }

            //PICTURE
            String pictureUri = "";
            if (result.getPhotos() != null && result.getPhotos().size() > 0) {
                pictureUri = RestaurantDataFormat.getPictureUri(result.getPhotos().get(0).photoReference);
            }

            //DISTANCE
            LatLng restaurantLatLng = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
            String distanceString = RestaurantDataFormat.getDistanceFromRestaurant(currentLatLng, restaurantLatLng);

            //RATING
            int ratingResource = RestaurantDataFormat.getRatingResource(result.getRating());

            restaurants.add(new RestaurantItem(
                    name,
                    placeId,
                    address,
                    hours,
                    pictureUri,
                    distanceString,
                    ratingResource
            ));

        }
        return restaurants;
    }


    public static List<RestaurantItem> convertPlaceToRestaurantItemList(Place place, LatLng currentLatLng) {

        if (place == null) return null;

        List<RestaurantItem> restaurants = new ArrayList<>();
        //NAME
        String name = place.getName();

        //ID
        String placeId = place.getId();

        //ADDRESS
        String address = place.getAddress();

        //OPENING HOURS
        String hours = RestaurantDataFormat.getHoursFromOpeningHours(place.getOpeningHours());

        //PICTURE
        String pictureUri = "";
        if (place.getPhotoMetadatas() != null && place.getPhotoMetadatas().size() > 0) {
            pictureUri = RestaurantDataFormat.getPictureUri(place.getPhotoMetadatas().get(0).zza());
        }

        //DISTANCE
        String distanceString = RestaurantDataFormat.getDistanceFromRestaurant(currentLatLng, place.getLatLng());

        //RATING
        double rate = place.getRating() != null ? place.getRating() : -1;
        int ratingResource = RestaurantDataFormat.getRatingResource((float) rate);


        restaurants.add(new RestaurantItem(
                name,
                placeId,
                address,
                hours,
                pictureUri,
                distanceString,
                ratingResource
        ));
        return restaurants;
    }
}

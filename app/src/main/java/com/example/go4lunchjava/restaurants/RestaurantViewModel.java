package com.example.go4lunchjava.restaurants;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel {

    //DEBUG
    private LatLng currentLatLng = new LatLng(40.7463956,-73.9852992);

    public static final String DUMMY_RESULT = "{\n" +
            "   \"html_attributions\" : [],\n" +
            "   \"results\" : [\n" +
            "      {\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 40.7466409,\n" +
            "               \"lng\" : -73.98543770000001\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 40.74806453029149,\n" +
            "                  \"lng\" : -73.98426226970849\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 40.74536656970849,\n" +
            "                  \"lng\" : -73.9869602302915\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"b23d23f0af627bac7bc0bd136401a13122ade6d7\",\n" +
            "         \"name\" : \"Café Feastro\",\n" +
            "         \"place_id\" : \"ChIJa5CNZqhZwokRpwX8CEX7HKc\",\n" +
            "         \"plus_code\" : {\n" +
            "            \"compound_code\" : \"P2W7+MR New York, États-Unis\",\n" +
            "            \"global_code\" : \"87G8P2W7+MR\"\n" +
            "         },\n" +
            "         \"reference\" : \"ChIJa5CNZqhZwokRpwX8CEX7HKc\",\n" +
            "         \"scope\" : \"GOOGLE\",\n" +
            "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "         \"vicinity\" : \"307 5th Avenue, New York\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 40.7464563,\n" +
            "               \"lng\" : -73.9854409\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 40.7479297802915,\n" +
            "                  \"lng\" : -73.98431506970849\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 40.7452318197085,\n" +
            "                  \"lng\" : -73.98701303029149\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"5a665663c82b664d73b423e87edab8e9f2cec5a3\",\n" +
            "         \"name\" : \"Bread and Butter\",\n" +
            "         \"opening_hours\" : {\n" +
            "            \"open_now\" : true\n" +
            "         },\n" +
            "         \"photos\" : [\n" +
            "            {\n" +
            "               \"height\" : 2268,\n" +
            "               \"html_attributions\" : [\n" +
            "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/107821815236197059478/photos\\\"\\u003esinan erel\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "               \"photo_reference\" : \"CmRaAAAAtSFL8Pjnif_K80dn-2GRL3s1livaidwsfmgl-e8SKgHninzn4PBQiOzkDBjv9TeK_7zx_wF-WdQ2_bNTwdHc-vKF9vFqDXBvM3JmX_k6gjKvznqCY5Q2USZFR3yrAV1xEhCT3_mn7ANA4yWn5SHIZN20GhRq_LYGvvrluV13qTNZSCbz4nnw7g\",\n" +
            "               \"width\" : 4032\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJRZJpYahZwokREPSRleELmCs\",\n" +
            "         \"plus_code\" : {\n" +
            "            \"compound_code\" : \"P2W7+HR New York, États-Unis\",\n" +
            "            \"global_code\" : \"87G8P2W7+HR\"\n" +
            "         },\n" +
            "         \"price_level\" : 1,\n" +
            "         \"rating\" : 0,\n" +
            "         \"reference\" : \"ChIJRZJpYahZwokREPSRleELmCs\",\n" +
            "         \"scope\" : \"GOOGLE\",\n" +
            "         \"types\" : [\n" +
            "            \"meal_takeaway\",\n" +
            "            \"cafe\",\n" +
            "            \"restaurant\",\n" +
            "            \"food\",\n" +
            "            \"point_of_interest\",\n" +
            "            \"establishment\"\n" +
            "         ],\n" +
            "         \"user_ratings_total\" : 1087,\n" +
            "         \"vicinity\" : \"303 5th Avenue, New York\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 40.74620109999999,\n" +
            "               \"lng\" : -73.9849079\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 40.74750868029149,\n" +
            "                  \"lng\" : -73.9835886697085\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 40.74481071970849,\n" +
            "                  \"lng\" : -73.98628663029152\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"cfdf81185b002407edce5d778d324d80969d4f4a\",\n" +
            "         \"name\" : \"Take 31\",\n" +
            "         \"opening_hours\" : {\n" +
            "            \"open_now\" : false\n" +
            "         },\n" +
            "         \"photos\" : [\n" +
            "            {\n" +
            "               \"height\" : 3024,\n" +
            "               \"html_attributions\" : [\n" +
            "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/112673185219288750384/photos\\\"\\u003eBrandon Chu\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "               \"photo_reference\" : \"CmRaAAAAw1hsVblAzMVFgV9mltWR4X2bIqplb2naRRNrKqeL9evpzj8fy9Ss4oZ1e25SEht2VGB-QQ0AzUhKGEs1U3y2wsU2BHOGuOPBEf1Uuw2raLK0zuF2bHCiXqSyWjZsmztXEhD5FzXOfHM3a0-LDSMK7GJ-GhRKXtuDZKYxt-NFiOuzhJxxsoE-aw\",\n" +
            "               \"width\" : 4032\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJ5-ZTbqhZwokROw_GJzQx1dw\",\n" +
            "         \"plus_code\" : {\n" +
            "            \"compound_code\" : \"P2W8+F2 New York, États-Unis\",\n" +
            "            \"global_code\" : \"87G8P2W8+F2\"\n" +
            "         },\n" +
            "         \"price_level\" : 2,\n" +
            "         \"rating\" : 4.399999999999999,\n" +
            "         \"reference\" : \"ChIJ5-ZTbqhZwokROw_GJzQx1dw\",\n" +
            "         \"scope\" : \"GOOGLE\",\n" +
            "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "         \"user_ratings_total\" : 334,\n" +
            "         \"vicinity\" : \"15 East 31st Street, New York\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 40.74632289999999,\n" +
            "               \"lng\" : -73.9849133\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 40.74758438029149,\n" +
            "                  \"lng\" : -73.98362721970848\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 40.74488641970849,\n" +
            "                  \"lng\" : -73.9863251802915\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"e13ea4b3f4238fa061e7c43a457b2021436db959\",\n" +
            "         \"name\" : \"LamaLo\",\n" +
            "         \"opening_hours\" : {\n" +
            "            \"open_now\" : true\n" +
            "         },\n" +
            "         \"photos\" : [\n" +
            "            {\n" +
            "               \"height\" : 3618,\n" +
            "               \"html_attributions\" : [\n" +
            "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/112457276566082201463/photos\\\"\\u003eLamaLo\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "               \"photo_reference\" : \"CmRaAAAAF-8N6wLlPgdkI-1t74MRr-edw8j_Fu5yDjd8j2-Zd4kKFLeOfW1reGdN0NXb5gqe3w7TqQwXmLMaQK-tIfs_sjc-cHW0piSv-eg5GO_ps9VSPJcuadkaxd3nxswK1_LqEhBFZ9QblpETLrspzMycn5xyGhQzVNX9glcyim19sbia_4BnfJ3VSw\",\n" +
            "               \"width\" : 4000\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJS1I0m0tZwokRzZtjrdEFNzc\",\n" +
            "         \"plus_code\" : {\n" +
            "            \"compound_code\" : \"P2W8+G2 New York, États-Unis\",\n" +
            "            \"global_code\" : \"87G8P2W8+G2\"\n" +
            "         },\n" +
            "         \"rating\" : 2,\n" +
            "         \"reference\" : \"ChIJS1I0m0tZwokRzZtjrdEFNzc\",\n" +
            "         \"scope\" : \"GOOGLE\",\n" +
            "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "         \"user_ratings_total\" : 48,\n" +
            "         \"vicinity\" : \"11 East 31st Street, New York\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 40.7467716,\n" +
            "               \"lng\" : -73.9855238\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 40.7481653302915,\n" +
            "                  \"lng\" : -73.9842787697085\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 40.7454673697085,\n" +
            "                  \"lng\" : -73.9869767302915\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"0e436ec9a2abcc7f88a88de0f8c31ea1feaa4403\",\n" +
            "         \"name\" : \"Let's Meat BBQ\",\n" +
            "         \"opening_hours\" : {\n" +
            "            \"open_now\" : false\n" +
            "         },\n" +
            "         \"photos\" : [\n" +
            "            {\n" +
            "               \"height\" : 1080,\n" +
            "               \"html_attributions\" : [\n" +
            "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117731144716034299994/photos\\\"\\u003eLet&#39;s Meat BBQ\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "               \"photo_reference\" : \"CmRaAAAAVNMONATN2jjhZvm3v3eTE1E_wcCZ5_AuaGcWhtnnKB3xlegmsTiDe8uRuKhRVnxHw_TwY7HJCJT8junZEJIwoDmBmCotoi500Hnbl40wt5FGgudMaAKXG0rNMhirdrm8EhC8Z98ChkgKciTm0vfiOoO6GhT_hzH_UTD4M0LmQDLXpi8Qvb8kLw\",\n" +
            "               \"width\" : 1920\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJJ4y_XahZwokRgO8olYwA7Cg\",\n" +
            "         \"plus_code\" : {\n" +
            "            \"compound_code\" : \"P2W7+PQ New York, États-Unis\",\n" +
            "            \"global_code\" : \"87G8P2W7+PQ\"\n" +
            "         },\n" +
            "         \"price_level\" : 2,\n" +
            "         \"rating\" : 4.3,\n" +
            "         \"reference\" : \"ChIJJ4y_XahZwokRgO8olYwA7Cg\",\n" +
            "         \"scope\" : \"GOOGLE\",\n" +
            "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "         \"user_ratings_total\" : 877,\n" +
            "         \"vicinity\" : \"307 5th Avenue, New York\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"geometry\" : {\n" +
            "            \"location\" : {\n" +
            "               \"lat\" : 40.74626879999999,\n" +
            "               \"lng\" : -73.9847815\n" +
            "            },\n" +
            "            \"viewport\" : {\n" +
            "               \"northeast\" : {\n" +
            "                  \"lat\" : 40.74752693029149,\n" +
            "                  \"lng\" : -73.98348766970848\n" +
            "               },\n" +
            "               \"southwest\" : {\n" +
            "                  \"lat\" : 40.74482896970849,\n" +
            "                  \"lng\" : -73.9861856302915\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
            "         \"id\" : \"1f747baed718f734ee3cb92e1f55ceeba6fea581\",\n" +
            "         \"name\" : \"Her Name is Han\",\n" +
            "         \"opening_hours\" : {\n" +
            "            \"open_now\" : false\n" +
            "         },\n" +
            "         \"photos\" : [\n" +
            "            {\n" +
            "               \"height\" : 3024,\n" +
            "               \"html_attributions\" : [\n" +
            "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/114185630651978875466/photos\\\"\\u003eJason McCann\\u003c/a\\u003e\"\n" +
            "               ],\n" +
            "               \"photo_reference\" : \"CmRaAAAA751RcHPMHKWIE0Zev-MWEmKb0EmdUmB-QJE9oOXhYAOmvybWjXUxHhldwpJ--jFtjyQ7q3rH2RkGou5Rw7mlL8wUjDgp7blKJpuzcc-nv1Nocwvvphv4sOSypTGSuPitEhAFi0vzoRa-dANweuRu6jVRGhR_xH3xRTHUkguNJgS1f83uo9Xp8Q\",\n" +
            "               \"width\" : 4032\n" +
            "            }\n" +
            "         ],\n" +
            "         \"place_id\" : \"ChIJKRVIbKhZwokRBg-yFy8VlKA\",\n" +
            "         \"plus_code\" : {\n" +
            "            \"compound_code\" : \"P2W8+G3 New York, États-Unis\",\n" +
            "            \"global_code\" : \"87G8P2W8+G3\"\n" +
            "         },\n" +
            "         \"price_level\" : 2,\n" +
            "         \"rating\" : 4.5,\n" +
            "         \"reference\" : \"ChIJKRVIbKhZwokRBg-yFy8VlKA\",\n" +
            "         \"scope\" : \"GOOGLE\",\n" +
            "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
            "         \"user_ratings_total\" : 1032,\n" +
            "         \"vicinity\" : \"17 East 31st Street, New York\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"status\" : \"OK\"\n" +
            "}";

    private MutableLiveData<List<RestaurantItem>> mRestaurantsMutableLiveData = new MutableLiveData<>();

    public RestaurantViewModel(){

        //TODO NINO: Peut-on récupérer la request du map view model ou faut-il nécessairement en effectuer une nouvelle ?
        getRestaurantsLiveData();
    }

    LiveData<List<RestaurantItem>> getRestaurantsLiveData(){

       // List<RestaurantItem> restaurants = new ArrayList<>();
       // restaurants.add(new RestaurantItem("Chen"));
       // restaurants.add(new RestaurantItem("Thai"));
       // restaurants.add(new RestaurantItem("Burger"));
       // restaurants.add(new RestaurantItem("Pâtes"));
       // restaurants.add(new RestaurantItem("Salades"));

        mRestaurantsMutableLiveData.setValue(getRestaurants());

        return mRestaurantsMutableLiveData;
    }

    private List<RestaurantItem> getRestaurants(){

        NearBySearchResponse response = mNearBySearchResponse();
        if (response == null) return null;


        List<RestaurantItem> restaurants = new ArrayList<>();
        for (NearBySearchResult result : response.results){

            String name = result.name;

            String address = result.vicinity;

            String hours = "Opening hours not communicated";
            if (result.openingHours != null){
                if (result.openingHours.openNow)hours = "Open now";
                else hours = "Closed";
            }

            String photoReference = "";
            if (result.photos != null && result.photos.size() > 0) {
                 photoReference = result.photos.get(0).photoReference;
            }

            float[] distanceResult = new float[1];
            Location.distanceBetween(result.geometry.location.lat, result.geometry.location.lng,
                    currentLatLng.latitude, currentLatLng.longitude, distanceResult);
            String distance = Math.round(distanceResult[0]) + "m";

            float rating = -1; //Negative value won't be take into account
            if (result.rating != null) rating = result.rating;

            restaurants.add(new RestaurantItem(
                    name,
                    address,
                    hours,
                    photoReference,
                    distance,
                    rating
                    ));

        }
        return restaurants;
    }

    //DEBUG
    private NearBySearchResponse mNearBySearchResponse(){

        Gson gson = new Gson();

        NearBySearchResponse response = gson.fromJson(DUMMY_RESULT, NearBySearchResponse.class);
        return response;
    }
}

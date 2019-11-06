package com.example.go4lunchjava.restaurant_details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.restaurant_details.pojo_api.RestaurantDetailsResponse;
import com.example.go4lunchjava.restaurant_details.pojo_api.RestaurantDetailsResult;
import com.google.gson.Gson;

public class RestaurantDetailsViewModel {

    //https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJJ4y_XahZwokRgO8olYwA7Cg&fields=name,photo,rating,vicinity,international_phone_number,website&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4

    private static final String DUMMY_JSON = "{\n" +
            "   \"html_attributions\" : [],\n" +
            "   \"result\" : {\n" +
            "      \"international_phone_number\" : \"+1 212-889-0089\",\n" +
            "      \"name\" : \"Let's Meat BBQ\",\n" +
            "      \"rating\" : 4.3,\n" +
            "      \"vicinity\" : \"307 5th Avenue, New York\",\n" +
            "      \"website\" : \"https://www.letsmeatnyc.com/\"\n" +
            "   },\n" +
            "   \"status\" : \"OK\"\n" +
            "}";

    private MutableLiveData<RestaurantDetailsResult> mDetailsMutableLiveData = new MutableLiveData<>();
    public LiveData<RestaurantDetailsResult> mDetailsLiveData = mDetailsMutableLiveData;

    public RestaurantDetailsViewModel(){


        getDetails(getResponse());
    }

    private void getDetails(RestaurantDetailsResponse response){

        mDetailsMutableLiveData.setValue(response.getResult());

    }

    //DEBUG
    private RestaurantDetailsResponse getResponse(){
        Gson gson = new Gson();
        return gson.fromJson(DUMMY_JSON, RestaurantDetailsResponse.class);
    }

}

package com.example.go4lunchjava.restaurant_details;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.restaurant_details.pojo_api.RestaurantDetailsResponse;
import com.example.go4lunchjava.restaurant_details.pojo_api.RestaurantDetailsResult;
import com.example.go4lunchjava.utils.RestaurantDataFormat;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

public class RestaurantDetailsViewModel extends ViewModel {

    //https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJJ4y_XahZwokRgO8olYwA7Cg&fields=name,photo,rating,vicinity,international_phone_number,website&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4

    private static final String DUMMY_JSON = "{\n" +
            "   \"html_attributions\" : [],\n" +
            "   \"result\" : {\n" +
            "      \"international_phone_number\" : \"+1 212-889-0089\",\n" +
            "      \"name\" : \"Let's Meat BBQ\",\n" +
            "      \"rating\" : 3.3,\n" +
            "      \"vicinity\" : \"307 5th Avenue, New York\",\n" +
            "      \"website\" : \"https://www.letsmeatnyc.com/\"\n" +
            "   },\n" +
            "   \"status\" : \"OK\"\n" +
            "}";

    private PlacesApiRepository mPlacesApiRepository;

    private MutableLiveData<RestaurantDetails> mDetailsMutableLiveData = new MutableLiveData<>();
    public LiveData<RestaurantDetails> mDetailsLiveData = mDetailsMutableLiveData;

    public RestaurantDetailsViewModel(){

        mPlacesApiRepository = PlacesApiRepository.getInstance();

    }

    public void launchDetailsRequest(String placeId){

        GetRestaurantDetailsAsyncTask asyncTask = new GetRestaurantDetailsAsyncTask(
                RestaurantDetailsViewModel.this, mPlacesApiRepository, placeId);
        asyncTask.execute();
    }

    private void getDetails(RestaurantDetailsResponse response){

        if (response == null) return; //TODO: Show an error message ?

        RestaurantDetailsResult result = response.getResult();

        //NAME
        String name = result.getName();
        //RATING
        int ratingImageResource = RestaurantDataFormat.getRatingResource(result.getRating());
        //ADDRESS
        String address = result.getVicinity();
        //PICTURE
        String pictureUri = "";
        if(result.getPhotos() != null && result.getPhotos().length > 0) {
            pictureUri = RestaurantDataFormat.getPictureUri(result.getPhotos()[0].getPhoto_reference());
        }
        //PHONE
        String phoneNumber = result.getInternational_phone_number();
        //WEB
        String webSite = result.getWebsite();

        RestaurantDetails restaurantDetails = new RestaurantDetails(
                name,
                ratingImageResource,
                address,
                pictureUri,
                phoneNumber,
                webSite
        );

        mDetailsMutableLiveData.setValue(restaurantDetails);
    }


    //DEBUG
    private RestaurantDetailsResponse getResponse(){
        Gson gson = new Gson();
        return gson.fromJson(DUMMY_JSON, RestaurantDetailsResponse.class);
    }

    private static class GetRestaurantDetailsAsyncTask extends AsyncTask<Void, Void, RestaurantDetailsResponse>{

        private WeakReference<RestaurantDetailsViewModel> mDetailsViewModelReference; //In case we loose ViewModel instance
        private PlacesApiRepository mPlacesApiRepository;
        private String mPlaceId;

        GetRestaurantDetailsAsyncTask(RestaurantDetailsViewModel detailsViewModel,
                                              PlacesApiRepository placesApiRepository, String placeId){

            mDetailsViewModelReference = new WeakReference<>(detailsViewModel);
            mPlacesApiRepository = placesApiRepository;
            mPlaceId = placeId;

        }

        @Override
        protected RestaurantDetailsResponse doInBackground(Void... voids) {
            return mPlacesApiRepository.getRestaurantDetailsResponse(mPlaceId);
        }

        @Override
        protected void onPostExecute(RestaurantDetailsResponse response) {
            super.onPostExecute(response);

            if (mDetailsViewModelReference.get() != null){
                mDetailsViewModelReference.get().getDetails(response);
            }
        }
    }

}

package com.example.go4lunchjava.places_api.pojo;

import com.google.android.gms.common.api.Result;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearBySearchResponse {

    @SerializedName("html_attributions")
    @Expose
    public List<Object> htmlAttributions = null;
    @SerializedName("results")
    @Expose
    public List<NearBySearchResult> results = null;
    @SerializedName("status")
    @Expose
    public String status;

}

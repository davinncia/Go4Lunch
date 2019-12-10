package com.example.go4lunchjava.places_api.pojo;

import androidx.annotation.VisibleForTesting;

import com.google.android.gms.common.api.Result;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearBySearchResponse {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("results")
    @Expose
    private List<NearBySearchResult> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    @VisibleForTesting
    public NearBySearchResponse(List<NearBySearchResult> results) {
        this.results = results;
    }

    public List<NearBySearchResult> getResults() {
        return results;
    }
}

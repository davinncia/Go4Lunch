package com.example.go4lunchjava.places_api.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlusCode {

    @SerializedName("compound_code")
    @Expose
    public String compoundCode;
    @SerializedName("global_code")
    @Expose
    public String globalCode;
}

package com.example.go4lunchjava.auth;

import java.util.List;

public class User {


    //Rmq: No member prefix for FireBase
    private String id;
    private String user_name;
    private String avatar_uri;
    private String restaurant_id;
    private String restaurant_name;
    private List<String> favorites;

    //Empty constructor required by FireStore
    public User(){}

    public User(String id, String userName, String avatarUri, String restaurantId, String restaurantName,
                List<String> favorites) {
        this.id = id;
        this.user_name = userName;
        this.avatar_uri = avatarUri;
        this.restaurant_id = restaurantId;
        this.restaurant_name = restaurantName;
        this.favorites = favorites;
    }

    public String getId() {
        return id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getAvatar_uri() {
        return avatar_uri;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public List<String> getFavorites() {
        return favorites;
    }

}

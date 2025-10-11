package com.marlodev.app_android.model;

import com.google.gson.Gson;

public class ProductWebSocketEvent {

    public Long id;
    public String name;
    public Double price;
    public String imageUrl;
    public Boolean isNew;
    public String action; // CREATE, UPDATE, DELETE, IMAGES_UPDATE

    public static ProductWebSocketEvent fromJson(String json) {
        return new Gson().fromJson(json, ProductWebSocketEvent.class);
    }
}

package com.marlodev.app_android.domain;

public class TagsModel {
    private String title;
    private int id;

    private String type;
    private String picUrl; //Eliminar más adelante

    public TagsModel(){

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}

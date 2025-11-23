package com.marlodev.app_android.viewmodel;

public class ItemCategoriaViewModel {
    private int id;
    private int icon;
    private String description;
    private String title;

    private String status;

    private  String createAt;
    public ItemCategoriaViewModel() {

    }

    public ItemCategoriaViewModel(int id, int icon, String description, String title, String status, String createAt) {
        this.id = id;
        this.icon = icon;
        this.description = description;
        this.title = title;
        this.status = status;
        this.createAt = createAt;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }


}

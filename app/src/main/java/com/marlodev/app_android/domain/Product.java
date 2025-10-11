package com.marlodev.app_android.domain;
import com.google.firebase.database.PropertyName;
import com.marlodev.app_android.model.ProductWebSocketEvent;
//import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private Long id;

    private int categoryId;
    private String createdAt;
    private int store;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {

        this.imageUrls = imageUrls;
    }

    public List<String> getImagePublicIds() {
        return imagePublicIds;
    }

    public void setImagePublicIds(List<String> imagePublicIds) {
        this.imagePublicIds = imagePublicIds;
    }

    private List<String> imageUrls;
    private List<String> imagePublicIds;


    private String updatedAt;
    private String name;
    private String description;
    private double price;
    private double oldPrice;
    private int discountPercent;
    private double rating;
    private int reviewsCount;
    private int sku;
    private List<String> tags;
    private List<ProductExtra> extras;
    private List<ProductImage> images;
    private ProductVariants variants;

    private boolean isNew;
    public Product() {
    }


    @PropertyName("isNew")
    public boolean isNew() {
        return isNew;
    }

    @PropertyName("isNew")
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }




    public long getId() {
        return id;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int     store) {
        this.store = store;
    }



    public void setId(long id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ProductExtra> getExtras() {
        return extras;
    }

    public void setExtras(List<ProductExtra> extras) {
        this.extras = extras;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public ProductVariants getVariants() {
        return variants;
    }

    public void setVariants(ProductVariants variants) {
        this.variants = variants;
    }







    // 🔁 Si tienes un método estático como fromWebSocketEvent:
    public static Product fromWebSocketEvent(com.marlodev.app_android.model.ProductWebSocketEvent event) {
        Product product = new Product();
        product.id = event.id;
        product.name = event.name;
        product.price = event.price;
        product.isNew = event.isNew;
        if (event.imageUrl != null && !event.imageUrl.trim().isEmpty()) {
            product.imageUrls = List.of(event.imageUrl);
        } else {
            product.imageUrls = new ArrayList<>(); // ← evita null
        }
        return product;
    }





}

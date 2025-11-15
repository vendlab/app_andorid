package com.marlodev.app_android.model;

import java.math.BigDecimal;
import java.util.List;

//import com.marlodev.app_android.model.extra.ExtraResponse;
//import com.marlodev.app_android.model.productVariant.ProductVariantResponse;
//import com.marlodev.app_android.model.tag.TagResponse;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;

    private BigDecimal price;
    private BigDecimal oldPrice;
    private Integer discountPercent;

    private Boolean isNew;
    private Double rating;
    private Integer reviewsCount;

    private Integer categoryId;
    private Integer storeId;

    private String createdAt; // puedes mapearlo luego a ZonedDateTime si quieres
    private String updatedAt;

    // 🔹 Relacionados
//    private List<ProductVariantResponse> variants;
//    private List<ExtraResponse> extras;
//    private List<TagResponse> tags;

    // 🔹 Imágenes
    private List<String> imageUrls;
    private List<String> imagePublicIds;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public Boolean getIsNew() { return isNew; }
    public void setIsNew(Boolean isNew) { this.isNew = isNew; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(Integer reviewsCount) { this.reviewsCount = reviewsCount; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getStoreId() { return storeId; }
    public void setStoreId(Integer storeId) { this.storeId = storeId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

//    public List<ProductVariantResponse> getVariants() { return variants; }
//    public void setVariants(List<ProductVariantResponse> variants) { this.variants = variants; }
//
//    public List<ExtraResponse> getExtras() { return extras; }
//    public void setExtras(List<ExtraResponse> extras) { this.extras = extras; }
//
//    public List<TagResponse> getTags() { return tags; }
//    public void setTags(List<TagResponse> tags) { this.tags = tags; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public List<String> getImagePublicIds() { return imagePublicIds; }
    public void setImagePublicIds(List<String> imagePublicIds) { this.imagePublicIds = imagePublicIds; }
}
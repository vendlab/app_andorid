package com.marlodev.app_android.model.order;


import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private String createdAt;
    private String updatedAt;

    // 🔹 Relacionados
//    private List<ProductVariantResponse> variants;
//    private List<ExtraResponse> extras;
//    private List<TagResponse> tags;

    // 🔹 Imágenes
    private List<String> imageUrls;
    private List<String> imagePublicIds;
}
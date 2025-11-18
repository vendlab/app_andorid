package com.marlodev.app_android.dto.product;


import com.marlodev.app_android.dto.ProductVariant.ProductVariantResponse;
import com.marlodev.app_android.dto.extra.ExtraResponse;
import com.marlodev.app_android.dto.tag.TagResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private List<ProductVariantResponse> variants;
    private List<ExtraResponse> extras;
    private List<TagResponse> tags;
    private List<String> imageUrls;
    private List<String> imagePublicIds;
}
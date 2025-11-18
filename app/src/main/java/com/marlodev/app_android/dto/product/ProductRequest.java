package com.marlodev.app_android.dto.product;
import com.marlodev.app_android.dto.ProductVariant.ProductVariantRequest;
import com.marlodev.app_android.dto.extra.ExtraRequest;
import com.marlodev.app_android.dto.tag.TagRequest;

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
public class ProductRequest {
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

    private List<ProductVariantRequest> variants;
    private List<ExtraRequest> extras;
    private List<TagRequest> tags;
    private List<String> imageUrls;
}
package com.marlodev.app_android.model.order;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
//    private ProductVariantResponse variant;
//    private List<ExtraResponse> extras;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

}
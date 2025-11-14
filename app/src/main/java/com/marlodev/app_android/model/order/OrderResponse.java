package com.marlodev.app_android.model.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {

    private Long id;
    private Integer userId;
    private String username;
    private String status;          // Enum como string
    private BigDecimal totalAmount;     // BigDecimal → double
    private String createdAt;       // ZonedDateTime → String
    private String updatedAt;
    private List<CartItemResponse> items;




    public Long getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }
}
package com.marlodev.app_android.model.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {

    private Long id;
    private Integer userId;
    private String username;
    private String status;          // Enum como string

    private String message;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
}
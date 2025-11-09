package com.marlodev.app_android.domain;

import com.google.firebase.database.PropertyName;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo principal de producto para la aplicación.
 * Representa tanto los datos almacenados en Firebase/REST como los recibidos vía WebSocket.
 */
@Data
public class Product {

    // --------------------------
    // 🔹 Identificación y fechas
    // --------------------------
    private Long id;
    private int categoryId;
    private String createdAt;
    private String updatedAt;
    private int store;

    // --------------------------
    // 🔹 Información visual
    // --------------------------
    private List<String> imageUrls;
    private List<String> imagePublicIds;
    private List<ProductImage> images;

    // --------------------------
    // 🔹 Información básica
    // --------------------------
    private String name;
    private String description;
    private double price;
    private double oldPrice;
    private int discountPercent;
    private double rating;
    private int reviewsCount;
    private int sku;

    private boolean newProduct;

    // --------------------------
    // 🔹 Información adicional
    // --------------------------
    private List<String> tags;
    private List<ProductExtra> extras;
    private ProductVariants variants;

    // --------------------------
    // 🔹 Conversión desde evento WebSocket
    // --------------------------
    public static Product fromWebSocketEvent(ProductWebSocketEvent event) {
        if (event == null) return null;

        Product product = new Product();
        product.setId(event.getId());
        product.setName(event.getName());
        product.setNewProduct(event.getNewProduct() != null && event.getNewProduct());
        product.setPrice(event.getPrice() != null ? event.getPrice() : 0.0);

        if (event.getImageUrl() != null && !event.getImageUrl().trim().isEmpty()) {
            product.setImageUrls(List.of(event.getImageUrl()));
        } else {
            product.setImageUrls(new ArrayList<>());
        }

        return product;
    }
}

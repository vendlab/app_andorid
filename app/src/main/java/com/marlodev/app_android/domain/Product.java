package com.marlodev.app_android.domain;

import com.google.firebase.database.PropertyName;
import com.marlodev.app_android.model.ProductWebSocketEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo principal de producto para la aplicación.
 * Representa tanto los datos almacenados en Firebase/REST como los recibidos vía WebSocket.
 */
public class Product {

    // --------------------------
    // 🔹 Identificación y fechas
    // --------------------------
    private Long id;               // ID único del producto
    private int categoryId;        // ID de la categoría a la que pertenece
    private String createdAt;      // Fecha de creación
    private String updatedAt;      // Fecha de actualización
    private int store;             // ID de la tienda

    // --------------------------
    // 🔹 Información visual
    // --------------------------
    private List<String> imageUrls;       // URLs de las imágenes del producto
    private List<String> imagePublicIds;  // IDs públicas de las imágenes en el storage
    private List<ProductImage> images;    // Lista de objetos ProductImage (si se necesita info detallada)

    // --------------------------
    // 🔹 Información básica
    // --------------------------
    private String name;          // Nombre del producto
    private String description;   // Descripción del producto
    private double price;         // Precio actual
    private double oldPrice;      // Precio anterior (para mostrar descuentos)
    private int discountPercent;  // Porcentaje de descuento
    private double rating;        // Rating promedio
    private int reviewsCount;     // Número de reseñas
    private int sku;              // Código SKU
    private boolean isNew;        // Indica si es un producto nuevo

    // --------------------------
    // 🔹 Información adicional
    // --------------------------
    private List<String> tags;              // Lista de tags del producto
    private List<ProductExtra> extras;      // Información extra (ej: tamaños, complementos)
    private ProductVariants variants;       // Variantes (ej: color, tamaño)

    // --------------------------
    // 🔹 Constructores
    // --------------------------
    public Product() {
    }

    // --------------------------
    // 🔹 Getters y Setters
    // --------------------------

    @PropertyName("isNew")  // Para mapear correctamente desde Firebase
    public boolean isNew() {
        return isNew;
    }

    @PropertyName("isNew")
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getStore() { return store; }
    public void setStore(int store) { this.store = store; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public void setTitle(String name) { this.name = name; } // Alias para nombre

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(int reviewsCount) { this.reviewsCount = reviewsCount; }

    public int getSku() { return sku; }
    public void setSku(int sku) { this.sku = sku; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<ProductExtra> getExtras() { return extras; }
    public void setExtras(List<ProductExtra> extras) { this.extras = extras; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    public ProductVariants getVariants() { return variants; }
    public void setVariants(ProductVariants variants) { this.variants = variants; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public List<String> getImagePublicIds() { return imagePublicIds; }
    public void setImagePublicIds(List<String> imagePublicIds) { this.imagePublicIds = imagePublicIds; }

    // --------------------------
    // 🔹 Conversión desde evento WebSocket
    // --------------------------
    /**
     * Convierte un evento recibido desde WebSocket en un objeto Product.
     * Esto permite que la app actualice la UI en tiempo real.
     *
     * @param event Evento WebSocket
     * @return Producto actualizado
     */
    public static Product fromWebSocketEvent(ProductWebSocketEvent event) {
        Product product = new Product();
        product.id = event.id;
        product.name = event.name;
        product.price = event.price;
        product.isNew = event.isNew;

        // Evita que imageUrls sea null
        if (event.imageUrl != null && !event.imageUrl.trim().isEmpty()) {
            product.imageUrls = List.of(event.imageUrl);
        } else {
            product.imageUrls = new ArrayList<>();
        }

        return product;
    }
}

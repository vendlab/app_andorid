package com.marlodev.app_android.model.order;

public class CartItem {
    private ProductResponse product; // la info del producto
    private int quantity;            // cantidad en el carrito

    public CartItem(ProductResponse product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // getters y setters
    public ProductResponse getProduct() { return product; }
    public void setProduct(ProductResponse product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
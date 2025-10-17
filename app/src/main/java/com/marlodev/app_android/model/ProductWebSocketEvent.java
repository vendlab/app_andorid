package com.marlodev.app_android.model;

import com.google.gson.Gson;

/**
 * Modelo que representa un evento recibido desde el WebSocket para productos.
 * Se utiliza para notificar cambios en tiempo real a la app.
 */
public class ProductWebSocketEvent {

    // --------------------------
    // 🔹 Campos del evento
    // --------------------------

    public Long id;          // ID del producto afectado
    public String name;      // Nombre del producto
    public Double price;     // Precio del producto
    public String imageUrl;  // URL de la imagen afectada (solo si es IMAGES_UPDATE)
    public Boolean isNew;    // Indica si el producto es nuevo
    public String action;    // Acción que indica qué pasó: CREATE, UPDATE, DELETE, IMAGES_UPDATE

    // --------------------------
    // 🔹 Método de utilidad
    // --------------------------

    /**
     * Convierte un JSON recibido desde el WebSocket en un objeto ProductWebSocketEvent.
     *
     * @param json String JSON del evento recibido
     * @return instancia de ProductWebSocketEvent con los datos parseados
     */
    public static ProductWebSocketEvent fromJson(String json) {
        return new Gson().fromJson(json, ProductWebSocketEvent.class);
    }
}

package com.marlodev.app_android.model;

import com.google.gson.Gson;
import lombok.Data;

/**
 * Modelo que representa un evento recibido desde el WebSocket para productos.
 * Se utiliza para notificar cambios en tiempo real a la app.
 */

@Data
public class ProductWebSocketEvent {

    // --------------------------
    // 🔹 Campos del evento
    // --------------------------
    private Long id;          // ID del producto afectado
    private String name;      // Nombre del producto
    private Double price;     // Precio del producto
    private String imageUrl;  // URL de la imagen afectada (solo si es IMAGES_UPDATE)
    private Boolean newProduct;    // Indica si el producto es nuevo
    private String action;    // Acción que indica qué pasó: CREATE, UPDATE, DELETE, IMAGES_UPDATE

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

package com.marlodev.app_android.domain;

/**
 * Modelo de datos para representar un Tag o etiqueta.
 * Se usa generalmente para categorizar productos o elementos en la app.
 */
public class Tag {

    private int id;       // Identificador único del tag
    private String title; // Título o nombre del tag

    // Constructor vacío requerido por Firebase o Gson
    public Tag() {
    }

    // ────────── Getters y Setters ──────────

    /**
     * Obtiene el ID del tag.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del tag.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el título del tag.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título del tag.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}

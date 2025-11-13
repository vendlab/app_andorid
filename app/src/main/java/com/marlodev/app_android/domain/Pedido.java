package com.marlodev.app_android.domain;

import java.io.Serializable;

/**
 * Representa un pedido asignado o disponible para el delivery.
 */
public class Pedido implements Serializable {

    private final int id;
    private final String cliente;
    private final String direccion;
    private final String distancia;


    public Pedido(int id, String cliente, String direccion, String distancia) {
        this.id = id;
        this.cliente = cliente;
        this.direccion = direccion;
        this.distancia = distancia;
    }

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getEstado() { return null;}
}

package com.marlodev.app_android.domain.delivery;

public class Pedido {
    private String nombreCliente;
    private String direccion;
    private double distanciaKm;
    private String tiempoEstimado;

    public Pedido(String nombreCliente, String direccion, double distanciaKm, String tiempoEstimado) {
        this.nombreCliente = nombreCliente;
        this.direccion = direccion;
        this.distanciaKm = distanciaKm;
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getNombreCliente() { return nombreCliente; }
    public String getDireccion() { return direccion; }
    public double getDistanciaKm() { return distanciaKm; }
    public String getTiempoEstimado() { return tiempoEstimado; }
}
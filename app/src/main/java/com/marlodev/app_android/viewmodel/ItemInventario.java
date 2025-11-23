package com.marlodev.app_android.viewmodel;

import com.marlodev.app_android.utils.TipoAlerta;

public class ItemInventario {
    private String nombre;
    private String ultimoReabastecimiento;
    private double stockActual;
    private double stockMinimo;
    private String unidad;
    private TipoAlerta tipoAlerta;

    public ItemInventario(String nombre, String ultimoReabastecimiento,
                          double stockActual, double stockMinimo,
                          String unidad, TipoAlerta tipoAlerta) {
        this.nombre = nombre;
        this.ultimoReabastecimiento = ultimoReabastecimiento;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.unidad = unidad;
        this.tipoAlerta = tipoAlerta;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getUltimoReabastecimiento() {
        return ultimoReabastecimiento;
    }

    public double getStockActual() {
        return stockActual;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public String getUnidad() {
        return unidad;
    }

    public TipoAlerta getTipoAlerta() {
        return tipoAlerta;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUltimoReabastecimiento(String ultimoReabastecimiento) {
        this.ultimoReabastecimiento = ultimoReabastecimiento;
    }

    public void setStockActual(double stockActual) {
        this.stockActual = stockActual;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public void setTipoAlerta(TipoAlerta tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    // Método para calcular el porcentaje de stock
    public int getPorcentajeStock() {
        if (stockMinimo == 0) return 0;
        return (int) ((stockActual / stockMinimo) * 100);
    }

    // Método para obtener el texto formateado del stock actual
    public String getStockActualFormateado() {
        if (stockActual % 1 == 0) {
            return (int) stockActual + " " + unidad;
        }
        return stockActual + " " + unidad;
    }

    // Método para obtener el texto formateado del stock mínimo
    public String getStockMinimoFormateado() {
        if (stockMinimo % 1 == 0) {
            return (int) stockMinimo + " " + unidad;
        }
        return stockMinimo + " " + unidad;
    }
}

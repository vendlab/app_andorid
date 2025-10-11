package com.marlodev.app_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.delivery.Pedido;
import com.marlodev.app_android.repository.delivery.PedidoRepository;

import java.util.List;

public class DashboardDelivery extends ViewModel {

    private PedidoRepository repository;
    private LiveData<List<Pedido>> pedidosDisponibles;

    public DashboardDelivery() {
        repository = new PedidoRepository();
        pedidosDisponibles = repository.obtenerPedidosDisponibles();
    }

    public LiveData<List<Pedido>> getPedidosDisponibles() {
        return pedidosDisponibles;
    }
}
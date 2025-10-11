package com.marlodev.app_android.repository.delivery;

import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.delivery.Pedido;

import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {

    public MutableLiveData<List<Pedido>> obtenerPedidosDisponibles() {
        MutableLiveData<List<Pedido>> data = new MutableLiveData<>();
        List<Pedido> pedidos = new ArrayList<>();

        pedidos.add(new Pedido("Juan Perez", "Av. Los Jazmines", 2.3, "15 min"));
        pedidos.add(new Pedido("Hugo Silva", "Av. Los Jazmines", 3.5, "20 min"));

        data.setValue(pedidos);
        return data;
    }
}
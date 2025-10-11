package com.marlodev.app_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.adapter.PedidoAdapter;
import com.marlodev.app_android.viewmodel.DashboardDelivery;

public class DashboardDeliveryActivity extends AppCompatActivity {

    private DashboardDelivery viewModel;
    private PedidoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_delivery);

        RecyclerView recyclerPedidos = findViewById(R.id.recyclerPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(DashboardDelivery.class);
        viewModel.getPedidosDisponibles().observe(this, pedidos -> {
            adapter = new PedidoAdapter(pedidos);
            recyclerPedidos.setAdapter(adapter);
        });
    }
}
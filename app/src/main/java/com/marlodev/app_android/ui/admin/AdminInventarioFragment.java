package com.marlodev.app_android.ui.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.FragmentAdminInventarioBinding;
import com.marlodev.app_android.utils.InventarioAdapter;
import com.marlodev.app_android.utils.TipoAlerta;
import com.marlodev.app_android.viewmodel.ItemInventario;

import java.util.ArrayList;
import java.util.List;

public class AdminInventarioFragment extends Fragment {

    private FragmentAdminInventarioBinding binding;
    private InventarioAdapter adapter;
    private List<ItemInventario> inventarioList;
    private List<ItemInventario> inventarioListFiltered;
    private String filtroActual = "TODOS"; // TODOS, CRITICO, ADVERTENCIA

    public AdminInventarioFragment() {
        // Required empty public constructor
    }

    public static AdminInventarioFragment newInstance() {
        return new AdminInventarioFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminInventarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadInventarioData();
        setupFilterButtons();
        setupReabastecerButton();
        updateFilterCounts();
    }

    private void setupRecyclerView() {
        binding.recyclerViewInventario.setLayoutManager(new LinearLayoutManager(getContext()));
        inventarioList = new ArrayList<>();
        inventarioListFiltered = new ArrayList<>();

        adapter = new InventarioAdapter(inventarioListFiltered, new InventarioAdapter.OnItemClickListener() {
            @Override
            public void onSolicitarReposicion(ItemInventario item) {
                // Aquí manejas la acción de solicitar reposición
                // Por ejemplo, mostrar un diálogo o navegar a otra pantalla
            }

            @Override
            public void onVerDetalles(ItemInventario item) {
                // Aquí manejas la acción de ver detalles
                // Por ejemplo, mostrar un diálogo con más información
            }
        });

        binding.recyclerViewInventario.setAdapter(adapter);
    }

    private void loadInventarioData() {
        // Aquí cargarías los datos desde tu base de datos o API
        // Por ahora, agregamos datos de ejemplo
        inventarioList.clear();

        inventarioList.add(new ItemInventario(
                "Granos de Café Arábica",
                "Reabastecido hace 3 días",
                2.0,
                10.0,
                "kg",
                TipoAlerta.CRITICO
        ));

        inventarioList.add(new ItemInventario(
                "Leche Entera",
                "Reabastecido ayer",
                8.0,
                15.0,
                "litros",
                TipoAlerta.ADVERTENCIA
        ));

        inventarioList.add(new ItemInventario(
                "Azúcar Blanca",
                "Reabastecido hace 4 días",
                1.5,
                5.0,
                "kg",
                TipoAlerta.CRITICO
        ));



        aplicarFiltro();
    }

    private void setupFilterButtons() {
        binding.btnCriticos.setOnClickListener(v -> {
            filtroActual = "CRITICO";
            aplicarFiltro();
            updateFilterButtonsUI();
        });

        binding.btnAdvertencias.setOnClickListener(v -> {
            filtroActual = "ADVERTENCIA";
            aplicarFiltro();
            updateFilterButtonsUI();
        });

        // Agregar un listener al contenedor para mostrar todos
        binding.textView2.setOnClickListener(v -> {
            filtroActual = "TODOS";
            aplicarFiltro();
            updateFilterButtonsUI();
        });
    }

    private void aplicarFiltro() {
        inventarioListFiltered.clear();

        if (filtroActual.equals("TODOS")) {
            inventarioListFiltered.addAll(inventarioList);
        } else if (filtroActual.equals("CRITICO")) {
            for (ItemInventario item : inventarioList) {
                if (item.getTipoAlerta() == TipoAlerta.CRITICO) {
                    inventarioListFiltered.add(item);
                }
            }
        } else if (filtroActual.equals("ADVERTENCIA")) {
            for (ItemInventario item : inventarioList) {
                if (item.getTipoAlerta() == TipoAlerta.ADVERTENCIA) {
                    inventarioListFiltered.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void updateFilterButtonsUI() {
        // Actualizar el estado visual de los botones según el filtro activo
        int colorCritico = getResources().getColor(android.R.color.white);
        int colorAdvertencia = getResources().getColor(android.R.color.white);

        if (filtroActual.equals("CRITICO")) {
            binding.btnCriticos.setAlpha(1.0f);
            binding.btnAdvertencias.setAlpha(0.5f);
        } else if (filtroActual.equals("ADVERTENCIA")) {
            binding.btnCriticos.setAlpha(0.5f);
            binding.btnAdvertencias.setAlpha(1.0f);
        } else {
            binding.btnCriticos.setAlpha(1.0f);
            binding.btnAdvertencias.setAlpha(1.0f);
        }
    }

    private void updateFilterCounts() {
        int countCriticos = 0;
        int countAdvertencias = 0;

        for (ItemInventario item : inventarioList) {
            if (item.getTipoAlerta() == TipoAlerta.CRITICO) {
                countCriticos++;
            } else if (item.getTipoAlerta() == TipoAlerta.ADVERTENCIA) {
                countAdvertencias++;
            }
        }

        binding.btnCriticos.setText(countCriticos + " Críticos");
        binding.btnAdvertencias.setText(countAdvertencias + " Advertencias");
    }

    private void setupReabastecerButton() {
        binding.button2.setOnClickListener(v -> {
            // Aquí manejas la acción de reabastecer
            // Por ejemplo, navegar a una pantalla de reabastecimiento
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.marlodev.app_android.ui.client;

import android.os.Bundle;

import androidx.annotation.NonNull; // Importación necesaria para el override
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marlodev.app_android.R;
import com.marlodev.app_android.utils.StatusBarUtil;
public class ClientOrderFragment extends Fragment {

    public ClientOrderFragment() {
        // Required empty public constructor
    }
    public static ClientOrderFragment newInstance() {
        return new ClientOrderFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflar el layout
        View view = inflater.inflate(R.layout.fragment_client_order, container, false);

        // Ajustar padding según barras del sistema (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        return view;
    }

}



package com.marlodev.app_android.ui.client;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.marlodev.app_android.R;
import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.utils.StatusBarUtil;

public class ClientPerfilFragment extends Fragment {

    private TextView tvEmail, tvRole, tvUserName;
    private Button btnLogout;

    public ClientPerfilFragment() {
        // Required empty public constructor
    }

    public static ClientPerfilFragment newInstance() {
        return new ClientPerfilFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_perfil, container, false);

        // Permitir que el contenido se dibuje debajo del status bar
        requireActivity().getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Inicializar vistas
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Cargar datos y configurar logout
        loadUserData();
        setupLogoutButton();

        // Ajustar padding inferior si es necesario (para la navegación)
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void loadUserData() {
        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();
        String username = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf("@"))
                : "Usuario";

        tvUserName.setText(username);
        tvEmail.setText(email != null ? email : "No disponible");
        tvRole.setText("Rol: " + (role != null ? role : "Invitado"));
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).clear();

            // Cerramos la activity contenedora y redirigimos a MainActivity
            requireActivity().finish();
            startActivity(new Intent(requireContext(), MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
    }




}

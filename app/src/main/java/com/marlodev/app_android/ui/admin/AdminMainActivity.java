package com.marlodev.app_android.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;
import com.marlodev.app_android.utils.SessionManager;

public class AdminMainActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvUserRole;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        // Ajuste visual EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadUserData();
        setupLogoutButton();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserRole = findViewById(R.id.tvUserRole);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        SessionManager session = SessionManager.getInstance(this);
        String email = session.getEmail();
        String role = session.getRole();

        // Nombre simulado a partir del correo
        String username = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf("@"))
                : "Administrador";

        tvUserName.setText(username);
        tvUserEmail.setText(email != null ? email : "correo@ejemplo.com");
        tvUserRole.setText("Rol: " + (role != null ? role : "Desconocido"));
    }

    /**
     * 🔹 Cierra sesión y redirige al MainActivity (no al login).
     */
    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            // Borrar toda la sesión
            SessionManager.getInstance(this).clear();

            // Redirigir a MainActivity como invitado
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

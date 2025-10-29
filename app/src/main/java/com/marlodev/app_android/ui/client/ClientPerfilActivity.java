package com.marlodev.app_android.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.marlodev.app_android.R;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.utils.SessionManager;

public class ClientPerfilActivity extends AppCompatActivity {

    private TextView tvEmail, tvRole, tvUserName;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_perfil);

        // Ajustar insets para diseño edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadUserData();
        setupLogoutButton();
    }

    /**
     * Inicializa las vistas de la interfaz.
     */
    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        btnLogout = findViewById(R.id.btnLogout);
    }

    /**
     * Carga los datos del usuario desde la sesión.
     */
    private void loadUserData() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();
        // Nombre simulado a partir del correo
        String username = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf("@"))
                : "Administrador";
        tvUserName.setText(username);
        tvEmail.setText(email != null ? email : "No disponible");
        tvRole.setText("Rol: " + (role != null ? role : "Invitado"));
    }

    /**
     * Configura el botón de cierre de sesión.
     * Al cerrar sesión redirige al MainActivity (no al Login).
     */
    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(this).clear();

            // Redirigir a MainActivity (pantalla principal sin sesión)
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

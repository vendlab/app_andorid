package com.marlodev.app_android.ui.client;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
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

        // Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_perfil);

        // ✅ Barra de estado transparente con íconos blancos
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT); // fondo transparente

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.getInsetsController().setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        } else {
            window.getDecorView().setSystemUiVisibility(
                    window.getDecorView().getSystemUiVisibility()
                            & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }


        // Ajustar insets para Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de vistas y datos
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

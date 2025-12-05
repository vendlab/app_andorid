package com.marlodev.app_android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.marlodev.app_android.R;
import com.marlodev.app_android.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initViewModel();

        // 🔹 Activar los ojitos de ver/ocultar contraseña
        setupPasswordToggle(etPassword, ivTogglePassword);
        setupPasswordToggle(etConfirmPassword, ivToggleConfirmPassword);

        // Registrar
        btnRegister.setOnClickListener(v -> attemptRegister());

        // Volver al login
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getRegisterStatus().observe(this, response -> {
            progressBar.setVisibility(View.GONE);

            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptRegister() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa tu nombre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        authViewModel.register(nombre, email, password);
    }

    // 🔹 Función para mostrar/ocultar contraseña
    private void setupPasswordToggle(EditText editText, ImageView toggleIcon) {

        toggleIcon.setOnClickListener(v -> {

            boolean isHidden = editText.getTransformationMethod() instanceof PasswordTransformationMethod;

            if (isHidden) {
                // Mostrar contraseña
                editText.setTransformationMethod(null);
                toggleIcon.setImageResource(R.drawable.icon_eye_open);
            } else {
                // Ocultar contraseña
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                toggleIcon.setImageResource(R.drawable.icon_eye_closed);
            }

            // Mantener cursor al final
            editText.setSelection(editText.getText().length());
        });
    }

}

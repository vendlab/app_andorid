package com.marlodev.app_android.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;
import com.marlodev.app_android.ui.admin.AdminDashboardActivity;
import com.marlodev.app_android.ui.barista.BaristaHomeActivity;
import com.marlodev.app_android.ui.delivery.DeliveryHomeActivity;
import com.marlodev.app_android.utils.JwtUtils;
import com.marlodev.app_android.viewmodel.AuthViewModel;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnLogin.setOnClickListener(v -> doLogin());

        authViewModel.getLoginToken().observe(this, token -> {
            if(token != null && !token.isEmpty()) {
                saveSession(token);
                navigateAccordingToRole(token);
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doLogin() {
        String username = etEmail.getText().toString().trim(); // ⚠️ Debe llamarse "username"
        String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Ingrese email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.login(username, password);
    }

    private void saveSession(String token){
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("isLoggedIn", true)
                .putString("token", token)
                .apply();
    }

    private void navigateAccordingToRole(String token){
        List<String> roles = JwtUtils.getRolesFromToken(token);

        Intent intent;
        if(roles.contains("ADMIN")){
            intent = new Intent(this, AdminDashboardActivity.class);
        } else if(roles.contains("BARISTA")){
            intent = new Intent(this, BaristaHomeActivity.class);
        } else if(roles.contains("DELIVERY")){
            intent = new Intent(this, DeliveryHomeActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class); // Cliente
        }

        startActivity(intent);
        finish();
    }
}

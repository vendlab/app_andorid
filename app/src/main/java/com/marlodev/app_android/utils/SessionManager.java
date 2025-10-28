package com.marlodev.app_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

/**
 * Gestiona la sesión del usuario (token, rol y correo).
 * Usado para mantener la autenticación en toda la app.
 */
public final class SessionManager {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMAIL = "email";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) instance = new SessionManager(context);
        return instance;
    }

    // Guardar token
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    // Obtener token actual
    @Nullable
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // Guardar rol
    public void saveRole(String role) {
        prefs.edit().putString(KEY_ROLE, role).apply();
    }

    // Obtener rol (por defecto CLIENT)
    public String getRole() {
        return prefs.getString(KEY_ROLE, "CLIENT");
    }

    // Guardar email
    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    // Obtener email actual
    @Nullable
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    // Cerrar sesión (elimina datos)
    public void clear() {
        prefs.edit().clear().apply();
    }

    // Saber si el usuario tiene sesión activa
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}

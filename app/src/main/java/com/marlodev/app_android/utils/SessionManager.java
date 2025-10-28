package com.marlodev.app_android.utils;

import android.content.Context;
import android.content.SharedPreferences;



public class SessionManager {
    private static final String PREF_NAME = "APP_PREFS";
    private static final String KEY_TOKEN = "AUTH_TOKEN";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveAuthToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getAuthToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}

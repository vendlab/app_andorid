package com.marlodev.app_android.utils;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

public final class JwtUtils {

    private JwtUtils() {}

    /**
     * Decodifica el payload del JWT (sin validar firma).
     * Retorna el payload como JSONObject o null si falla.
     */
    public static JSONObject decodePayload(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            byte[] decoded = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            String json = new String(decoded);
            return new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extrae un role (primer role) si existe.
     */
    public static String extractFirstRole(String jwt) {
        try {
            JSONObject payload = decodePayload(jwt);
            if (payload == null) return null;
            if (payload.has("roles")) {
                Object rolesObj = payload.get("roles");
                if (rolesObj instanceof JSONArray) {
                    JSONArray arr = payload.getJSONArray("roles");
                    if (arr.length() > 0) return arr.getString(0);
                } else if (rolesObj instanceof String) {
                    return payload.getString("roles");
                }
            }
            // alternativa común: "authorities" o "role"
            if (payload.has("authorities")) {
                JSONArray arr = payload.getJSONArray("authorities");
                if (arr.length() > 0) return arr.getString(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

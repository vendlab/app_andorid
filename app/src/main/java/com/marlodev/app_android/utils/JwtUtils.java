package com.marlodev.app_android.utils;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JwtUtils {

    public static List<String> getRolesFromToken(String token) {
        if (token == null || token.isEmpty()) return Collections.emptyList();

        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return Collections.emptyList();

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(payloadJson);

            List<String> roles = new ArrayList<>();
            if (payload.has("roles")) {
                JSONArray rolesArray = payload.getJSONArray("roles");
                for (int i = 0; i < rolesArray.length(); i++) {
                    roles.add(rolesArray.getString(i));
                }
            } else if (payload.has("role")) {
                roles.add(payload.getString("role"));
            }
            return roles;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

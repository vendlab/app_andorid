package com.marlodev.app_android.model;



import com.google.gson.annotations.SerializedName;

// Clase que mapea la respuesta del backend: { "access_token": "..." }
public class LoginResponse {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}

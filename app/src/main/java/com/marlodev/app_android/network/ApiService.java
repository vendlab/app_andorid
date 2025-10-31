package com.marlodev.app_android.network;

import com.marlodev.app_android.model.LoginRequest;
import com.marlodev.app_android.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    // 🔐 AUTENTICACIÓN
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Otros endpoints generales pueden ir aquí
}

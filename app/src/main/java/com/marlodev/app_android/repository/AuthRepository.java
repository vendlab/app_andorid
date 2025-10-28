package com.marlodev.app_android.repository;


import android.util.Log;

import com.marlodev.app_android.model.LoginRequest;
import com.marlodev.app_android.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * AuthRepository
 * -------------------------------------------------
 * Maneja login, logout y persistencia del token JWT
 */
public class AuthRepository {


    private static final String BASE_URL = "https://tu-backend.com/api/auth/"; // 🔹 Cambia esto
    private final AuthApi authApi;

    public AuthRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
    }

    public LiveData<LoginResponse> login(String email, String password) {
        MutableLiveData<LoginResponse> data = new MutableLiveData<>();
        LoginRequest request = new LoginRequest(email, password);

        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                    Log.e("AuthRepository", "Login error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                data.setValue(null);
                Log.e("AuthRepository", "Error en login: " + t.getMessage());
            }
        });

        return data;
    }

    interface AuthApi {
        @Headers("Content-Type: application/json")
        @POST("login") // 🔹 Ajusta según tu endpoint
        Call<LoginResponse> login(@Body LoginRequest request);
    }


}


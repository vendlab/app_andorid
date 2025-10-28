package com.marlodev.app_android.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.model.LoginRequest;
import com.marlodev.app_android.model.LoginResponse;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.ApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<String> loginToken = new MutableLiveData<>();
    private final MutableLiveData<String> registerResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<String> getLoginToken() { return loginToken; }
    public LiveData<String> getRegisterResult() { return registerResult; }

    public void login(String username, String password) {
        apiService.login(new LoginRequest(username, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getAccessToken();
                            Log.d("AuthViewModel", "✅ Login exitoso. Token recibido: " + token);
                            loginToken.postValue(token);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "sin cuerpo";
                                Log.e("AuthViewModel", "❌ Error en login. Código: " + response.code() + ", cuerpo: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            loginToken.postValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e("AuthViewModel", "💥 Falla en conexión: " + t.getMessage());
                        loginToken.postValue(null);
                    }
                });
    }


}
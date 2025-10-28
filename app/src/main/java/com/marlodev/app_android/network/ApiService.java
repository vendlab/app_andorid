package com.marlodev.app_android.network;

import com.marlodev.app_android.model.LoginRequest;
import com.marlodev.app_android.model.LoginResponse;
import com.marlodev.app_android.domain.Product; // ejemplo

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Ejemplo de endpoint protegido que necesitará Authorization header
    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") long id);
}

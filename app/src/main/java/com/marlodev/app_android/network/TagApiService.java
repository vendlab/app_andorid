package com.marlodev.app_android.network;

import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.dto.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Servicio de API para manejar Tags.
 * Define las llamadas HTTP hacia el backend.
 */
public interface TagApiService {

    @GET("/api/admin/tags")
    Call<ApiResponse<List<Tag>>> getTags();

    @GET("/api/admin/tags/{id}")
    Call<ApiResponse<Tag>> getTagById(@Path("id") Integer id);
}
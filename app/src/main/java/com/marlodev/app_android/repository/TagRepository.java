package com.marlodev.app_android.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.dto.ApiResponse;
import com.marlodev.app_android.network.TagApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio profesional para Tags.
 * Gestiona REST API y mantiene LiveData sincronizado.
 * Ideal para MVVM y LiveData.
 */
public class TagRepository {

    private static final String TAG_LOG = "TagRepository";

    private final TagApiService apiService;

    private final MutableLiveData<List<Tag>> _tags = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    public final LiveData<List<Tag>> tags = _tags;
    public final LiveData<String> errorMessage = _errorMessage;
    public final LiveData<Boolean> isLoading = _isLoading;

    public TagRepository(TagApiService apiService) {
        this.apiService = apiService;
    }

    // -----------------------------
    // REST API
    // -----------------------------
    /**
     * Carga todos los tags desde el backend
     */
    public void loadTags() {
        _isLoading.postValue(true);
        apiService.getTags().enqueue(new Callback<ApiResponse<List<Tag>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Tag>>> call, Response<ApiResponse<List<Tag>>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Tag>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        _tags.postValue(apiResponse.getData());
                        Log.d(TAG_LOG, "✅ Tags cargados: " + apiResponse.getData().size());
                    } else {
                        _errorMessage.postValue(apiResponse.getMessage());
                        Log.e(TAG_LOG, "⚠️ Error al cargar tags: " + apiResponse.getMessage());
                    }
                } else {
                    _errorMessage.postValue("Error al cargar tags (" + response.code() + ")");
                    Log.e(TAG_LOG, "⚠️ Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Tag>>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de red: " + t.getMessage());
                Log.e(TAG_LOG, "❌ Falló la carga de tags", t);
            }
        });
    }

    /**
     * Obtiene un tag por ID
     * @param id ID del tag
     * @return LiveData<Tag> con el resultado o null si no existe
     */
    public LiveData<Tag> getTagById(Integer id) {
        MutableLiveData<Tag> liveData = new MutableLiveData<>();
        List<Tag> list = _tags.getValue();
        if (list != null) {
            Tag local = list.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
            if (local != null) {
                liveData.postValue(local);
                return liveData;
            }
        }

        apiService.getTagById(id).enqueue(new Callback<ApiResponse<Tag>>() {
            @Override
            public void onResponse(Call<ApiResponse<Tag>> call, Response<ApiResponse<Tag>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(response.body().getData());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Tag>> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }
}
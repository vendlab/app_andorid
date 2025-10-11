package com.marlodev.app_android.network;

import com.marlodev.app_android.domain.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductApiService {


    @GET("admin/products")
    Call<List<Product>> getProducts();
}


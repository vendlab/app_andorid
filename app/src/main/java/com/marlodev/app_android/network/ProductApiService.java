package com.marlodev.app_android.network;

import com.marlodev.app_android.domain.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApiService {

    // 🛍️ PRODUCTOS
    @GET("admin/products")
    Call<List<Product>> getProducts();

    @GET("admin/products/{id}")
    Call<Product> getProductById(@Path("id") long id);
}

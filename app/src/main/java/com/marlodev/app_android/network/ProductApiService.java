package com.marlodev.app_android.network;

// Importaciones necesarias
import com.marlodev.app_android.domain.Product; // Modelo de datos Product
import java.util.List; // Para manejar listas de productos
import retrofit2.Call; // Retrofit usa Call para manejar peticiones HTTP
import retrofit2.http.GET; // Anotación para indicar que este método hace una petición GET

// Interfaz que define los endpoints de la API relacionados con productos
public interface ProductApiService {

    // Define un endpoint GET hacia "admin/products"
    // Retrofit lo convierte en una llamada HTTP que retorna una lista de productos
    @GET("admin/products")
    Call<List<Product>> getProducts(); // Devuelve un Call de Retrofit con una lista de objetos Product
}

package com.marlodev.app_android.network;

// Importaciones necesarias para Retrofit y OkHttp
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Clase para manejar la instancia de Retrofit (cliente HTTP) de manera singleton
public class ApiClient {

    // URL base de la API. "10.0.2.2" se usa para acceder al localhost desde un emulador Android
    private static final String BASE_URL = "http://10.0.2.2:5050/api/";

    // Instancia única de Retrofit (singleton)
    private static Retrofit retrofit;

    // Método para obtener la instancia de Retrofit
    public static Retrofit getRetrofitInstance() {
        // Si la instancia aún no se ha creado
        if (retrofit == null) {

            // Interceptor para registrar las peticiones y respuestas HTTP (útil para debugging)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Muestra headers y body

            // Cliente HTTP con el interceptor agregado
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Construcción de la instancia de Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // URL base de la API
                    .client(client) // Cliente HTTP con logging
                    .addConverterFactory(GsonConverterFactory.create()) // Conversión JSON <-> Objetos Java
                    .build();
        }

        // Retornamos la instancia de Retrofit
        return retrofit;
    }
}

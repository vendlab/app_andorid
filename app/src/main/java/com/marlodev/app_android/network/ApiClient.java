package com.marlodev.app_android.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public final class ApiClient {

    private static volatile Retrofit retrofit;
    private static final Object LOCK = new Object();

    // Cambia la base URL según tu entorno; para emulador: http://10.0.2.2:5050/api/
    private static String BASE_URL = "http://10.0.2.2:5050/api/";

    private ApiClient() {}

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (LOCK) {
                if (retrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .addInterceptor(new AuthInterceptor(context)) // inyecta token automáticamente
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    // Si necesitas cambiar la base URL en runtime (p. ej. entorno prod/dev)
    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        retrofit = null; // fuerza reconstrucción en next getClient
    }
}

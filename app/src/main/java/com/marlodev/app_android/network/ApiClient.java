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
    private static String BASE_URL = "https://ecommerce-backend-o9y5.onrender.com/api/";


    private ApiClient() {}

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (LOCK) {
                if (retrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .addInterceptor(new AuthInterceptor(context))
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

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        retrofit = null;
    }
}

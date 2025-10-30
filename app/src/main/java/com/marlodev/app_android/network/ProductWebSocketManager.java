package com.marlodev.app_android.network;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.marlodev.app_android.model.ProductWebSocketEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class ProductWebSocketManager {

    private static final String TAG = "ProductWebSocket";
    private final StompClient stompClient;
    private final MutableLiveData<ProductWebSocketEvent> productEventLiveData = new MutableLiveData<>();
    private final Gson gson = new Gson();
    private Disposable subscription;
    private final String wsUrl;
    private final String token;

    public ProductWebSocketManager(String wsUrl, String token) {
        this.wsUrl = wsUrl;
        this.token = token;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        // Escucha los eventos del ciclo de vida del WebSocket
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            Log.d(TAG, "📡 Estado WS: " + lifecycleEvent.getType());
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.i(TAG, "✅ WebSocket conectado correctamente");
                    subscribeToTopic();
                    break;
                case ERROR:
                    Log.e(TAG, "❌ Error en WebSocket", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.w(TAG, "⚠️ WebSocket cerrado, reintentando en 5s...");
                    reconnect();
                    break;
                default:
                    Log.d(TAG, "ℹ️ Otro estado: " + lifecycleEvent.getType());
            }
        });
    }

    private void subscribeToTopic() {
        Log.i(TAG, "📡 Subscribiéndose a /topic/products...");
        subscription = stompClient.topic("/topic/products")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    ProductWebSocketEvent event = gson.fromJson(topicMessage.getPayload(), ProductWebSocketEvent.class);
                    Log.i(TAG, "🔔 Evento recibido: " + event.getAction() + " → " + event.getName());
                    productEventLiveData.postValue(event);
                }, throwable -> Log.e(TAG, "❌ Error al recibir evento WS", throwable));
    }

    private void reconnect() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                connect();
            } catch (InterruptedException e) {
                Log.e(TAG, "Error en reconexión", e);
            }
        }).start();
    }

    public void connect() {
        List<StompHeader> headers = new ArrayList<>();
        if (token != null) headers.add(new StompHeader("Authorization", "Bearer " + token));

        Log.i(TAG, "🚀 Conectando al WebSocket: " + wsUrl);
        stompClient.connect(headers);
    }

    public void disconnect() {
        if (subscription != null && !subscription.isDisposed()) subscription.dispose();
        stompClient.disconnect();
        Log.i(TAG, "❎ Desconectado del WebSocket");
    }

    public LiveData<ProductWebSocketEvent> getProductEventLiveData() {
        return productEventLiveData;
    }
}

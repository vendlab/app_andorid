package com.marlodev.app_android.network;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.marlodev.app_android.model.ProductWebSocketEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

/**
 * ProductWebSocketManager (Enterprise)
 * -------------------------------------------------
 * 🔹 Conexión segura con STOMP + JWT.
 * 🔹 Suscripción a /topic/products.
 * 🔹 Reconexión automática con backoff.
 * -------------------------------------------------
 */
public class ProductWebSocketManager {

    private static final String TAG = "ProductWebSocket";
    private static final String WS_URL = "ws://10.0.2.2:5050/ws-products/websocket";

    private final String jwtToken;
    private final Gson gson = new Gson();
    private final MutableLiveData<ProductWebSocketEvent> productEventLiveData = new MutableLiveData<>();
    private StompClient stompClient;
    private Disposable lifecycleDisposable;
    private Disposable topicDisposable;

    public ProductWebSocketManager(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public LiveData<ProductWebSocketEvent> getProductEventLiveData() {
        return productEventLiveData;
    }

    public void connect() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);

        List<StompHeader> headers = new ArrayList<>();
        if (jwtToken != null && !jwtToken.isEmpty()) {
            headers.add(new StompHeader("Authorization", "Bearer " + jwtToken));
        }

        stompClient.connect(headers);

        lifecycleDisposable = stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "✅ Conexión WebSocket abierta");
                    subscribeToProductTopic();
                    break;
                case ERROR:
                    Log.e(TAG, "⚠️ Error en WebSocket", lifecycleEvent.getException());
                    reconnectWithDelay();
                    break;
                case CLOSED:
                    Log.w(TAG, "🔌 Conexión cerrada. Reintentando...");
                    reconnectWithDelay();
                    break;
            }
        });
    }

    private void subscribeToProductTopic() {
        if (topicDisposable != null && !topicDisposable.isDisposed()) topicDisposable.dispose();

        topicDisposable = stompClient.topic("/topic/products").subscribe(stompMessage -> {
            try {
                ProductWebSocketEvent event = gson.fromJson(stompMessage.getPayload(), ProductWebSocketEvent.class);
                productEventLiveData.postValue(event);
            } catch (Exception e) {
                Log.e(TAG, "Error parseando mensaje WebSocket", e);
            }
        });
    }

    private void reconnectWithDelay() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                connect();
            } catch (InterruptedException e) {
                Log.e(TAG, "Error en reconexión WebSocket", e);
            }
        }).start();
    }

    public void disconnect() {
        if (topicDisposable != null) topicDisposable.dispose();
        if (lifecycleDisposable != null) lifecycleDisposable.dispose();
        if (stompClient != null && stompClient.isConnected()) stompClient.disconnect();
    }
}

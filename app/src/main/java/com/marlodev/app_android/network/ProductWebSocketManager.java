package com.marlodev.app_android.network;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.marlodev.app_android.model.ProductWebSocketEvent;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompMessage;

public class ProductWebSocketManager {
    private static final String TAG = "ProductWebSocket";
    private static final String WS_URL = "ws://10.0.2.2:5050/ws-products/websocket";
    private StompClient stompClient;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<ProductWebSocketEvent> productEventLiveData = new MutableLiveData<>();

    public void connect() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);
        stompClient.connect();

        // Escuchar ciclo de vida
        Disposable lifecycleDisposable = stompClient.lifecycle().subscribe(event -> {
            switch (event.getType()) {
                case OPENED:
                    Log.i(TAG, "✅ WebSocket conectado");
                    break;
                case ERROR:
                    Log.e(TAG, "❌ Error WebSocket", event.getException());
                    break;
                case CLOSED:
                    Log.w(TAG, "⚠️ WebSocket cerrado");
                    break;
            }
        });
        compositeDisposable.add(lifecycleDisposable);

        // Suscripción al tópico
        Disposable topicDisposable = stompClient.topic("/topic/products").subscribe(msg -> {
            String payload = msg.getPayload();
            try {
                ProductWebSocketEvent event = ProductWebSocketEvent.fromJson(payload);
                productEventLiveData.postValue(event);
            } catch (Exception e) {
                Log.e(TAG, "Error parseando mensaje: " + payload, e);
            }
        });
        compositeDisposable.add(topicDisposable);
    }
    public void disconnect() {
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.disconnect();
        }
        compositeDisposable.clear();
    }
}

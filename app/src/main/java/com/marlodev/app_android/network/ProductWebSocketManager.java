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

// Clase encargada de manejar la conexión WebSocket para productos
public class ProductWebSocketManager {

    // TAG para logs
    private static final String TAG = "ProductWebSocket";

    // URL del WebSocket (localhost de emulador Android)
    private static final String WS_URL = "ws://10.0.2.2:5050/ws-products/websocket";

    // Cliente STOMP
    private StompClient stompClient;

    // Manejo de múltiples disposables de RxJava para limpiar suscripciones
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // LiveData que expone eventos de productos recibidos vía WebSocket
    public final MutableLiveData<ProductWebSocketEvent> productEventLiveData = new MutableLiveData<>();


    // Conecta al WebSocket y configura listeners
    public void connect() {
        // Crear cliente STOMP usando OKHTTP
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);

        // Conectar al WebSocket
        stompClient.connect();

        // Suscribirse al ciclo de vida del WebSocket
        Disposable lifecycleDisposable = stompClient.lifecycle().subscribe(event -> {
            switch (event.getType()) {
                case OPENED:
                    // Conexión establecida
                    Log.i(TAG, "✅ WebSocket conectado");
                    break;
                case ERROR:
                    // Error de conexión
                    Log.e(TAG, "❌ Error WebSocket", event.getException());
                    break;
                case CLOSED:
                    // WebSocket cerrado
                    Log.w(TAG, "⚠️ WebSocket cerrado");
                    break;
            }
        });
        // Agregar disposable para limpiarlo cuando se desconecte
        compositeDisposable.add(lifecycleDisposable);

        // Suscribirse a un tópico específico "/topic/products" para recibir mensajes
        Disposable topicDisposable = stompClient.topic("/topic/products").subscribe(msg -> {
            String payload = msg.getPayload(); // mensaje en formato JSON
            try {
                // Parsear JSON a objeto ProductWebSocketEvent
                ProductWebSocketEvent event = ProductWebSocketEvent.fromJson(payload);

                // Publicar evento en LiveData para que la UI pueda observarlo
                productEventLiveData.postValue(event);
            } catch (Exception e) {
                // Error al parsear el mensaje
                Log.e(TAG, "Error parseando mensaje: " + payload, e);
            }
        });
        // Agregar disposable para limpiar la suscripción
        compositeDisposable.add(topicDisposable);
    }

    // Desconectar del WebSocket y limpiar disposables
    public void disconnect() {
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.disconnect();
        }
        compositeDisposable.clear(); // limpiar todas las suscripciones
    }
}

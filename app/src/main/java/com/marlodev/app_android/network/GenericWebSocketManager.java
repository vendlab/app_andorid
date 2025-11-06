package com.marlodev.app_android.network;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

/**
 * Generic WebSocket Manager
 *
 * Permite suscribirse a cualquier topic de STOMP de manera genérica y obtener
 * los eventos parseados en objetos tipo T.
 *
 * @param <T> Tipo de evento (ProductWebSocketEvent, UserWebSocketEvent, etc.)
 */
public class GenericWebSocketManager<T> {

    private static final String TAG = "GenericWebSocket";

    private final StompClient stompClient;
    private final MutableLiveData<T> eventLiveData = new MutableLiveData<>();
    private final Gson gson = new Gson();
    private final Class<T> eventClass;
    private final String wsUrl;
    private final String token;
    private final String topic;

    private Disposable subscription;

    public GenericWebSocketManager(String wsUrl, String token, String topic, Class<T> eventClass) {
        this.wsUrl = wsUrl;
        this.token = token;
        this.topic = topic;
        this.eventClass = eventClass;

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        // Monitoreo del lifecycle del WebSocket
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
            }
        });
    }

    /** Suscripción al topic */
    private void subscribeToTopic() {
        if (subscription != null && !subscription.isDisposed()) subscription.dispose();

        Log.i(TAG, "📡 Subscribiéndose a " + topic);
        subscription = stompClient.topic(topic)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    try {
                        T event = gson.fromJson(message.getPayload(), eventClass);
                        eventLiveData.postValue(event);
                        Log.d(TAG, "🔔 Evento recibido en " + topic + ": " + event);
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parseando evento WS", e);
                    }
                }, throwable -> Log.e(TAG, "❌ Error al recibir evento WS", throwable));
    }

    /** Reconexión automática tras cierre */
    private void reconnect() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                connect();
            } catch (InterruptedException e) {
                Log.e(TAG, "❌ Error en reconexión WS", e);
            }
        }).start();
    }


    public void connect() {
        List<StompHeader> headers = new ArrayList<>();

        if (token != null && !token.isEmpty()) {
            headers.add(new StompHeader("Authorization", "Bearer " + token));
            Log.i(TAG, "📌 Headers enviados: Authorization: Bearer " + token);
        } else {
            Log.w(TAG, "⚠️ Token nulo o vacío, conectando al WS de forma anónima");
        }

        Log.i(TAG, "🚀 Conectando al WebSocket: " + wsUrl);
        stompClient.connect(headers);
    }


    /** Desconecta y libera recursos */
    public void disconnect() {
        if (subscription != null && !subscription.isDisposed()) subscription.dispose();
        if (stompClient != null) stompClient.disconnect();
        Log.i(TAG, "❎ Desconectado del WebSocket");
    }

    /** Devuelve un LiveData para observar eventos */
    public LiveData<T> getEventLiveData() {
        return eventLiveData;
    }
}

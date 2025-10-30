package com.marlodev.app_android.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.ProductWebSocketManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends AndroidViewModel {

    private static final String TAG = "ProductViewModel";

    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private ProductWebSocketManager webSocketManager;

    public ProductViewModel(@NonNull Application application) {
        super(application);
    }

    // 🔹 LiveData de productos
    public LiveData<List<Product>> getProducts() {
        return products;
    }

    // 🔹 Estado de carga (ProgressBar)
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // 🔹 LiveData de eventos WebSocket
    public LiveData<ProductWebSocketEvent> getProductEventLiveData() {
        if (webSocketManager != null) return webSocketManager.getProductEventLiveData();
        return new MutableLiveData<>();
    }

    // 🔹 Cargar productos desde API REST
    public void loadProducts() {
        isLoading.setValue(true);
        Log.d(TAG, "📦 Cargando productos desde la API REST...");

        ProductApiService service = ApiClient.getClient(getApplication()).create(ProductApiService.class);
        service.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Productos cargados: " + response.body().size());
                    products.postValue(response.body());
                } else {
                    Log.e(TAG, "⚠️ Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "❌ Falló la carga de productos: " + t.getMessage(), t);
            }
        });
    }

    // 🔹 Manejo de eventos WebSocket (CREATE, UPDATE, DELETE, IMAGES_UPDATE)
// ProductViewModel.java

    public void handleWebSocketEvent(ProductWebSocketEvent event) {
        // 1. Verificación inicial de nulidad del evento
        if (event == null || event.getAction() == null) return;

        List<Product> currentProducts = products.getValue() != null
                ? new ArrayList<>(products.getValue())
                : new ArrayList<>();

        // 2. Manejo especial para el evento DELETE
        // Si la acción es DELETE, asumimos que el ID es la única información necesaria
        // y evitamos llamar a Product.fromWebSocketEvent(event) que causa la NPE.
        if ("DELETE".equals(event.getAction())) {
            // En el escenario de 'DELETE → null' (tu logcat), NO podemos obtener el ID.
            // **Este es el punto más débil:** Si el backend envía 'DELETE → null',
            // no podemos eliminar el producto de la lista local.

            // Dado que tu código original intentaba crear un Producto, vamos a forzar
            // la creación *solo* para obtener el ID, PERO esto requiere que primero
            // *corrijas el método Product.fromWebSocketEvent* para que no crashee con nulos.

            // **Opción más segura y simple para evitar el crash AHORA:** Simplemente salir,
            // asumiendo que el borrado fallará en la lista local si el backend no manda el ID.

            // **Corrigiendo el flujo para ser robusto:** Intentamos crear el producto SOLO para
            // obtener el ID. Si falla (NPE en Product.java), este bloque nunca se alcanza.

            // Si no quieres modificar Product.java, debes cambiar la lógica del backend
            // o manejar la extracción del ID aquí directamente.
            // Asumiendo que has corregido el NPE en Product.java, el flujo se mantiene:

            // *********************************************************************************
            // SI YA HAS CORREGIDO Product.java:143 (para que no crashee con payload nulo):
            // *********************************************************************************
            Product productToDelete = Product.fromWebSocketEvent(event);
            if (productToDelete != null && productToDelete.getId() != null) {
                currentProducts.removeIf(p -> p.getId().equals(productToDelete.getId()));
                Log.d(TAG, "🔴 Producto eliminado: " + productToDelete.getId());
            } else {
                Log.e(TAG, "❌ Evento DELETE con payload inválido (null o sin ID). Ignorado.");
            }

            // Si el evento DELETE se maneja aquí, actualizamos la lista y terminamos el método
            products.postValue(currentProducts);
            return;
        }

        // 3. Manejo de CREATE, UPDATE y otros eventos
        // Solo para CREATE y UPDATE, necesitamos todos los datos del producto.
        Product product = Product.fromWebSocketEvent(event);

        if (product == null || product.getId() == null) {
            // Si el payload es nulo o inválido para CREATE/UPDATE, salimos.
            Log.w(TAG, "⚠️ Payload nulo/sin ID para acción: " + event.getAction() + ". Ignorado.");
            return;
        }

        switch (event.getAction()) {
            case "CREATE":
                boolean exists = false;
                for (Product p : currentProducts) {
                    if (p.getId().equals(product.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    currentProducts.add(product);
                    Log.d(TAG, "🟢 Producto agregado: " + product.getName());
                }
                break;

            case "UPDATE":
            case "IMAGES_UPDATE":
                for (int i = 0; i < currentProducts.size(); i++) {
                    if (currentProducts.get(i).getId().equals(product.getId())) {
                        currentProducts.set(i, product);
                        Log.d(TAG, "🟡 Producto actualizado: " + product.getName());
                        break;
                    }
                }
                break;

            default:
                Log.w(TAG, "⚪ Acción desconocida: " + event.getAction());
                break;
        }

        // Actualizamos LiveData → notifica al RecyclerView
        products.postValue(currentProducts);
    }
    // 🔹 Inicializa WebSocket con URL y token
    public void initWebSocket(String wsUrl, String token) {
        Log.d(TAG, "🌐 Inicializando WebSocket con URL: " + wsUrl);

        if (webSocketManager == null) {
            webSocketManager = new ProductWebSocketManager(wsUrl, token);
            webSocketManager.connect(); // 🔥 Importante: inicia conexión real
        } else {
            Log.w(TAG, "⚠️ WebSocket ya estaba inicializado");
        }
    }

    // 🔹 Desconexión
    public void disconnectWebSocket() {
        if (webSocketManager != null) {
            Log.d(TAG, "🔌 Cerrando conexión WebSocket...");
            webSocketManager.disconnect();
        }
    }

    // 🔹 Obtener producto por ID
    // Archivo: ProductViewModel.java

    // 🔹 Obtener producto por ID (IMPLEMENTACIÓN CORREGIDA)
    public LiveData<Product> getProductById(long id) {
        // 1. Crea un LiveData para el producto individual
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();

        ProductApiService service = ApiClient.getClient(getApplication()).create(ProductApiService.class);

        // 2. Llama al servicio de la API REST para obtener el producto por ID
        service.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Detalles de producto cargados para ID: " + id);
                    // 3. Publica el resultado en el LiveData
                    productLiveData.postValue(response.body());
                } else {
                    Log.e(TAG, "⚠️ Error al cargar detalles de producto (código " + response.code() + ") para ID: " + id);
                    productLiveData.postValue(null); // Notificar error/nulo
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "❌ Falló la conexión para detalles de producto: " + t.getMessage(), t);
                productLiveData.postValue(null); // Notificar fallo de conexión
            }
        });

        // 4. Devuelve el LiveData inmediatamente.
        return productLiveData;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        disconnectWebSocket();
    }
}

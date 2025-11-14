package com.marlodev.app_android.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.model.order.CartItem;
import com.marlodev.app_android.model.order.CartItemResponse;
import com.marlodev.app_android.model.order.OrderResponse;
import com.marlodev.app_android.model.order.ProductResponse;
import com.marlodev.app_android.repository.CartRepository;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.order.CartApi;

import java.util.ArrayList;
import java.util.List;

public class ClientCartVM extends AndroidViewModel {

    private final CartRepository repository;

    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);

    public ClientCartVM(@NonNull Application app) {
        super(app);
        CartApi api = ApiClient.getClient(app).create(CartApi.class);
        repository = new CartRepository(api);
        loadCart();
    }

    public LiveData<List<CartItem>> getCartItems() { return cartItems; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getTotalItems() { return totalItems; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }

    public void loadCart() {
        isLoading.setValue(true);
        repository.getCart(new CartRepository.SimpleCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                List<CartItem> list = new ArrayList<>();
                if (result != null && result.getItems() != null) {
                    for (CartItemResponse item : result.getItems()) {
                        ProductResponse product = item.getProduct();
                        int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                        list.add(new CartItem(product, qty));
                    }
                }
                cartItems.postValue(list);
                recalcTotals(list);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void updateQuantity(CartItem item, int newQty) {
        item.setQuantity(newQty);
        cartItems.setValue(cartItems.getValue()); // refresca LiveData
        recalcTotals(cartItems.getValue());
        // Aquí también puedes llamar al backend: repository.updateItem(...)
    }

    public void deleteItem(CartItem item) {
        repository.deleteItem(item.getProduct().getId(), new CartRepository.SimpleCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                List<CartItem> current = new ArrayList<>(cartItems.getValue());
                current.remove(item);
                cartItems.postValue(current);
                recalcTotals(current);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    private void recalcTotals(List<CartItem> items) {
        if (items == null) return;
        int count = 0;
        double total = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
            if (item.getProduct().getPrice() != null)
                total += item.getProduct().getPrice().doubleValue() * item.getQuantity();
        }
        totalItems.postValue(count);
        totalPrice.postValue(total);
    }
}

package com.marlodev.app_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.model.order.OrderResponse;
import com.marlodev.app_android.repository.OrderRepository;

import java.util.List;

public class ClientOrderViewModel extends ViewModel {

    private final OrderRepository repository;
    private final MutableLiveData<List<OrderResponse>> activeOrders = new MutableLiveData<>();
    private final MutableLiveData<List<OrderResponse>> historyOrders = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ClientOrderViewModel(OrderRepository repository) {
        this.repository = repository;
        loadActiveOrders();
        loadHistoryOrders();
    }

    public LiveData<List<OrderResponse>> getActiveOrders() { return activeOrders; }
    public LiveData<List<OrderResponse>> getHistoryOrders() { return historyOrders; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadActiveOrders() {
        repository.getActiveOrders(new OrderRepository.SimpleCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> result) { activeOrders.postValue(result); }
            @Override
            public void onError(String message) { errorMessage.postValue(message); }
        });
    }

    public void loadHistoryOrders() {
        repository.getOrderHistory(new OrderRepository.SimpleCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> result) { historyOrders.postValue(result); }
            @Override
            public void onError(String message) { errorMessage.postValue(message); }
        });
    }
}

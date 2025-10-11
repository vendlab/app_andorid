package com.marlodev.app_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {



//
//
//
//    private final ProductRepository repository = new ProductRepository();
//    private final MutableLiveData<ArrayList<Product>> productsLiveData = new MutableLiveData<>();
//
//    public ProductViewModel() {
//        loadProducts();
//    }
//
//    public LiveData<ArrayList<Product>> getProducts() {
//        return productsLiveData;
//    }
//
//    private void loadProducts() {
//        repository.getProducts().observeForever(products -> productsLiveData.setValue(products));
//    }



    private final ProductRepository repository = new ProductRepository();

    public LiveData<ArrayList<Product>> getProducts() {
        return repository.getProducts();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.disconnect();
    }



    }

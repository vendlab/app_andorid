package com.marlodev.app_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.BannerModel;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.domain.TagsModel;
import com.marlodev.app_android.repository.MainRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private final MainRepository repository = new MainRepository();
    public LiveData<ArrayList<TagsModel>> loadCategory() {
        return repository.loadTags();
    }
    public LiveData<ArrayList<BannerModel>> loadBanner() {
        return repository.loadBanners();
    }
    public LiveData<ArrayList<Product>> loadPopular() {
        return repository.loadPopular();
    }
}
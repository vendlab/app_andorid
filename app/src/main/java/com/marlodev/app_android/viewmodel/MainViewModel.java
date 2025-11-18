package com.marlodev.app_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.BannerModel;
import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.repository.MainRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    private final MainRepository firebaseRepository = new MainRepository();

    public LiveData<ArrayList<Tag>> loadCategory() {
        return firebaseRepository.loadTags();
    }

    public LiveData<ArrayList<BannerModel>> loadBanner() {
        return firebaseRepository.loadBanners();
    }



}
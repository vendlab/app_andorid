package com.marlodev.app_android.repository;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlodev.app_android.domain.BannerModel;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.domain.TagsModel;

import java.util.ArrayList;

public class MainRepository {
    private final FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
    public LiveData<ArrayList<TagsModel>> loadTags(){
        MutableLiveData<ArrayList<TagsModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("tags");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TagsModel> list = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    TagsModel item = childSnapshot.getValue(TagsModel.class);
                    if (item != null)  list.add(item);
                }
                listData.setValue(list);

            }

            @Override
            public  void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return listData;
    }

    public LiveData<ArrayList<BannerModel>> loadBanners(){
        MutableLiveData<ArrayList<BannerModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("banners");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BannerModel> list = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    BannerModel item = childSnapshot.getValue(BannerModel.class);
                    if (item != null)  list.add(item);
                }
                listData.setValue(list);

            }

            @Override
            public  void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return listData;
    }



}

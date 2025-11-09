
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
import com.marlodev.app_android.domain.TagsModel;

import java.util.ArrayList;

// Clase encargada de interactuar con Firebase Realtime Database
public class MainRepository {

    // Instancia de FirebaseDatabase
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    /**
     * Carga las categorías (tags) desde Firebase.
     * @return LiveData con lista de TagsModel para ser observado desde ViewModel
     */
    public LiveData<ArrayList<TagsModel>> loadTags() {
        // MutableLiveData para exponer los datos a la UI
        MutableLiveData<ArrayList<TagsModel>> listData = new MutableLiveData<>();

        // Referencia a la ruta "tags" en Firebase
        DatabaseReference ref = firebaseDatabase.getReference("tags");

        // Listener que se activa cuando hay cambios en los datos
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TagsModel> list = new ArrayList<>();
                // Iterar sobre cada hijo en la ruta "tags"
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Convertir snapshot a objeto TagsModel
                    TagsModel item = childSnapshot.getValue(TagsModel.class);
                    if (item != null) list.add(item); // agregar si no es nulo
                }
                // Actualizar LiveData con la lista
                listData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de Firebase (actualmente vacío)
            }
        });

        return listData; // Retornar LiveData observable
    }

    /**
     * Carga los banners desde Firebase.
     * @return LiveData con lista de BannerModel para ser observado desde ViewModel
     */
    public LiveData<ArrayList<BannerModel>> loadBanners() {
        MutableLiveData<ArrayList<BannerModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("banners");

        // Listener que se activa cuando hay cambios en los datos
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BannerModel> list = new ArrayList<>();
                // Iterar sobre cada hijo en la ruta "banners"
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    BannerModel item = childSnapshot.getValue(BannerModel.class);
                    if (item != null) list.add(item);
                }
                // Actualizar LiveData con la lista
                listData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de Firebase (actualmente vacío)
            }
        });

        return listData; // Retornar LiveData observable
    }
}


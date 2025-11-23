package com.marlodev.app_android.ui.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.marlodev.app_android.R;
import com.marlodev.app_android.utils.ItemCategoriaAdapter;
import com.marlodev.app_android.viewmodel.ItemCategoriaViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminTagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminTagsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminTagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminTagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminTagsFragment newInstance(String param1, String param2) {
        AdminTagsFragment fragment = new AdminTagsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_tags, container, false);
        //Estos datos son de prueba luego se tienen que reemplazar por los que vienen de la base de datos
        List<ItemCategoriaViewModel> itemList = new ArrayList<>();
        itemList.add(new ItemCategoriaViewModel(1, R.drawable.ic_coffe, "Capuchino, expresso, mocca y latte macchiato.", "Cafés", "Activa", "2025-09-22"));
        itemList.add(new ItemCategoriaViewModel(2, R.drawable.ic_frappe, "Mocha blanco, caramel, moras, matcha y platano", "Frappes", "Activa", "2025-09-22"));
        itemList.add(new ItemCategoriaViewModel(3, R.drawable.ic_bakery, "Panes", "Panes", "Activa", "2025-09-22"));
        // De acá en adelante no se toca nada
        RecyclerView rv = view.findViewById(R.id.recyclerView);
        TextInputLayout searchLayout = view.findViewById(R.id.search_layout);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemCategoriaAdapter adapter = new ItemCategoriaAdapter(getContext(), itemList);
        rv.setAdapter(adapter);
        TextInputEditText searchEdit = (TextInputEditText) searchLayout.getEditText();

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;

    }
}
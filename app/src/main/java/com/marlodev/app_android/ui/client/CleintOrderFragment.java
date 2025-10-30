package com.marlodev.app_android.ui.client;

import android.os.Bundle;

import androidx.annotation.NonNull; // Importación necesaria para el override
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marlodev.app_android.R;
import com.marlodev.app_android.utils.StatusBarUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CleintOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CleintOrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CleintOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CleintOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CleintOrderFragment newInstance(String param1, String param2) {
        CleintOrderFragment fragment = new CleintOrderFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cliente_carrito, container, false);

        // 🎯 AJUSTE DE INSETS: Solo aplica padding superior (Status Bar).
        // El padding inferior se elimina para evitar conflictos y saltos con la ChipNavigationBar.
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Aplicar padding solo al TOP (Status Bar)
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        // 🛑 CORRECCIÓN CRÍTICA: Devolver la vista inflada.
        return view;
    }
}

//    @Override
//    public void onResume() {
//        super.onResume();
//        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.colorBlack, false);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.colorGreen1, true);
//    }

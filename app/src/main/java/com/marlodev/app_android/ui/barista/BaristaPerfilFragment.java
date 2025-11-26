package com.marlodev.app_android.ui.barista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.databinding.FragmentBaristaPerfilBinding;
import com.marlodev.app_android.utils.SessionManager;

public class BaristaPerfilFragment extends Fragment {

    private FragmentBaristaPerfilBinding binding;

    public BaristaPerfilFragment() { }

    public static BaristaPerfilFragment newInstance() {
        return new BaristaPerfilFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBaristaPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();
        setupLogoutButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadUserData() {
        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        String email = sessionManager.getEmail();

        String username = "Barista";
        if (email != null && email.contains("@")) {
            username = email.substring(0, email.indexOf("@"));
        }

        binding.tvBaristaName.setText(username);
        binding.tvBaristaEmail.setText(email != null ? email : "No disponible");
    }

    private void setupLogoutButton() {
        binding.btnBaristaLogout.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).clear();

            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            requireActivity().finish();
        });
    }
}

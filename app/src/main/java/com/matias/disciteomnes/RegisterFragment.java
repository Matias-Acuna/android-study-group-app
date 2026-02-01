package com.matias.disciteomnes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.matias.disciteomnes.databinding.FragmentRegisterBinding;
import com.matias.disciteomnes.model.RegisterRequest;
import com.matias.disciteomnes.model.ApiResponse;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Fragment responsible for handling user registration
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use ViewBinding to inflate the layout
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        // Set click listener on the register button
        binding.registerButton.setOnClickListener(v -> registerUser());

        return binding.getRoot();
    }

    // Method to validate input and perform user registration
    private void registerUser() {
        String name = binding.nameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        // Basic input validation
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the request object with user input
        RegisterRequest request = new RegisterRequest();
        request.name = name;
        request.email = email;
        request.password = password;

        // Call the backend API to register the user
        DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
        api.registerUser(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // Registration successful → redirect to login
                    Toast.makeText(getContext(), "Registration successful. You can now log in.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    // API responded but with failure
                    Toast.makeText(getContext(), "Registration error: " + (response.body() != null ? response.body().message : "unknown"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Network or server failure
                Toast.makeText(getContext(), "Network failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

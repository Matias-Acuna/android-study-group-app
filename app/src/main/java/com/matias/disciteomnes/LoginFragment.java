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

import com.matias.disciteomnes.databinding.FragmentLoginBinding;
import com.matias.disciteomnes.model.LoginRequest;
import com.matias.disciteomnes.model.LoginResponse;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Fragment that handles user login functionality
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        // Set listener on login button
        binding.loginButton.setOnClickListener(v -> loginUser());

        return binding.getRoot();
    }

    // Method to validate and send login request to the server
    private void loginUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        // Basic input validation
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build the login request
        LoginRequest request = new LoginRequest(email, password);

        // Make API call using Retrofit
        DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
        api.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse login = response.body();

                    // If login was successful and token is returned
                    if (login.success && login.token != null) {
                        String token = login.token;
                        String userName = login.name;
                        String userId = login.userId;

                        // Save token, user name, and ID in shared preferences
                        requireActivity()
                                .getSharedPreferences("APP_PREFS", getContext().MODE_PRIVATE)
                                .edit()
                                .putString("JWT_TOKEN", token)
                                .putString("USER_NAME", userName)
                                .putString("USER_ID", userId)
                                .apply();

                        Toast.makeText(getContext(), "Welcome " + userName, Toast.LENGTH_SHORT).show();

                        // Redirect to dashboard
                        startActivity(new Intent(getActivity(), DashboardActivity.class));
                        requireActivity().finish();
                    } else {
                        // Wrong credentials
                        Toast.makeText(getContext(), "Incorrect credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Server returned an error
                    Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Network or unexpected error
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

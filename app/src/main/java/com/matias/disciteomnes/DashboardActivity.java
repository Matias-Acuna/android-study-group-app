package com.matias.disciteomnes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.matias.disciteomnes.adapter.GroupAdapter;
import com.matias.disciteomnes.model.Group;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Main dashboard where users can view, create, and manage study groups
public class DashboardActivity extends AppCompatActivity {

    private static final int CREATE_GROUP_REQUEST_CODE = 1001;

    private FloatingActionButton addGroupButton;
    private RecyclerView groupRecyclerView;
    private GroupAdapter groupAdapter;
    private TextView userWelcomeText;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Display the user's name in a welcome message
        userWelcomeText = findViewById(R.id.userWelcomeText);
        String userName = getSharedPreferences("APP_PREFS", MODE_PRIVATE).getString("USER_NAME", null);
        if (userName != null) {
            userWelcomeText.setText("Hello, " + userName + "!");
        }

        // Logout logic
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("APP_PREFS", MODE_PRIVATE).edit();
            editor.remove("JWT_TOKEN");
            editor.remove("USER_NAME");
            editor.apply();

            // Redirect to login screen
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Floating button to open the group creation screen
        addGroupButton = findViewById(R.id.addGroupButton);
        addGroupButton.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, CreateGroupActivity.class);
            startActivityForResult(intent, CREATE_GROUP_REQUEST_CODE);
        });

        // Setup RecyclerView for listing groups
        groupRecyclerView = findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(new ArrayList<>(), null);
        groupRecyclerView.setAdapter(groupAdapter);

        // Load groups from backend
        fetchGroups();
    }

    // Method to fetch all available groups from the backend server.
    // This method sends an authenticated GET request using the saved JWT token.
    // If the request is successful, it populates the RecyclerView with the list of groups.
    // If the token is missing or invalid, it redirects the user to the login screen.
    private void fetchGroups() {
        // Get the JWT token from shared preferences and prefix it with "Bearer"
        String token = "Bearer " + getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                .getString("JWT_TOKEN", "");

        // If the token is empty (user is not authenticated), redirect to login screen
        if (token.equals("Bearer ")) {
            Toast.makeText(this, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Create an instance of the API interface
        DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);

        // Make an asynchronous API call to retrieve the list of groups
        api.getGroups(token).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                // If the response is successful (status code 200)
                if (response.isSuccessful()) {
                    List<Group> groups = response.body();

                    // If the list is empty or null, show a message and clear adapter data
                    if (groups == null || groups.isEmpty()) {
                        Toast.makeText(DashboardActivity.this, "No groups yet. Create one!", Toast.LENGTH_SHORT).show();
                        groupAdapter.setGroups(new ArrayList<>());
                    } else {
                        // Otherwise, update the adapter with the list of groups
                        groupAdapter.setGroups(groups);
                    }

                } else {
                    // Handle HTTP errors
                    int code = response.code();
                    if (code == 401) {
                        // Unauthorized access — token might be expired or invalid
                        Toast.makeText(DashboardActivity.this, "Unauthorized. Please log in again.", Toast.LENGTH_LONG).show();
                    } else {
                        // General server error with code and message
                        Toast.makeText(DashboardActivity.this, "Error: " + code + " - " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                // Handle connectivity or unexpected failures
                Toast.makeText(DashboardActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 🔁 Reload the group list when returning from CreateGroupActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_GROUP_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchGroups(); // 🔄 Refresh group list
        }
    }
}

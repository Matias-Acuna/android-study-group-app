package com.matias.disciteomnes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.matias.disciteomnes.model.Group;
import com.matias.disciteomnes.model.GroupRequest;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity for creating a new study group
public class CreateGroupActivity extends AppCompatActivity {

    private EditText nameInput, descriptionInput;
    private Button createGroupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Bind UI components
        nameInput = findViewById(R.id.groupNameInput);
        descriptionInput = findViewById(R.id.groupDescriptionInput);
        createGroupBtn = findViewById(R.id.createGroupButton);

        // Handle create group button click
        createGroupBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            // Check for empty fields
            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve JWT token from shared preferences
            String token = "Bearer " + getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                    .getString("JWT_TOKEN", "");

            // Build the group request
            GroupRequest request = new GroupRequest(name, description);

            // Execute API call to create the group
            DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
            api.createGroup(token, request).enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CreateGroupActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK); // 🔁 So DashboardActivity can refresh when returning
                        finish();             // 🔙 Go back to previous screen
                    } else {
                        Toast.makeText(CreateGroupActivity.this, "Error creating group: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    Toast.makeText(CreateGroupActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}

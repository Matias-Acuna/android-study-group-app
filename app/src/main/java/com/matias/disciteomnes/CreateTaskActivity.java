package com.matias.disciteomnes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.matias.disciteomnes.model.Task;
import com.matias.disciteomnes.model.TaskRequest;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity to create a new task within a group
public class CreateTaskActivity extends AppCompatActivity {

    private EditText titleInput, dateInput;
    private Button createTaskButton;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Retrieve group ID from intent
        groupId = getIntent().getStringExtra("group_id");

        // Bind UI elements
        titleInput = findViewById(R.id.taskTitleInput);
        dateInput = findViewById(R.id.taskDateInput);
        createTaskButton = findViewById(R.id.createTaskButton);

        // 👇 Date picker dialog for due date selection
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                        dateInput.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        // Handle create task button click
        createTaskButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String dueDate = dateInput.getText().toString().trim();

            // Validate task title input
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title for the task", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve authentication token from shared preferences
            String token = "Bearer " + getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                    .getString("JWT_TOKEN", "");

            // Create task request object
            TaskRequest request = new TaskRequest();
            request.title = title;
            request.dueDate = dueDate; // ✅ Set the selected date

            // Send API request to create the task
            DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
            api.addTask(token, groupId, request).enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CreateTaskActivity.this, "Task created successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity and return to previous screen
                    } else {
                        Toast.makeText(CreateTaskActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    Toast.makeText(CreateTaskActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}

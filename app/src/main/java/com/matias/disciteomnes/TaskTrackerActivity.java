package com.matias.disciteomnes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.matias.disciteomnes.adapter.TaskAdapter;
import com.matias.disciteomnes.model.Task;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity responsible for displaying the list of tasks assigned to a user
 * within a specific group, and for launching the task creation screen.
 */
public class TaskTrackerActivity extends AppCompatActivity {

    private RecyclerView userTasksRecyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton addTaskButton;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_tracker);

        // Retrieve the group ID passed from the previous activity (e.g. Dashboard)
        groupId = getIntent().getStringExtra("group_id");

        // Setup RecyclerView with a linear vertical layout
        userTasksRecyclerView = findViewById(R.id.userTasksRecyclerView);
        userTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and assign adapter to handle the list of tasks
        taskAdapter = new TaskAdapter();
        userTasksRecyclerView.setAdapter(taskAdapter);

        // FloatingActionButton allows user to add a new task manually
        addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> {
            // Navigate to CreateTaskActivity, passing the current group ID
            Intent intent = new Intent(TaskTrackerActivity.this, CreateTaskActivity.class);
            intent.putExtra("group_id", groupId);
            startActivity(intent);
        });

        // Load current tasks for this group from the backend
        fetchTasks();
    }

    /**
     * Calls the backend API to retrieve all tasks associated with the current group.
     * Handles different response outcomes including authentication issues and empty lists.
     */
    private void fetchTasks() {
        // Retrieve JWT token for authenticated API calls
        String token = "Bearer " + getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                .getString("JWT_TOKEN", "");

        // Initialize Retrofit API client
        DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);

        // Perform asynchronous API request to get tasks for the selected group
        api.getTasks(token, groupId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    List<Task> tasks = response.body();

                    if (tasks == null || tasks.isEmpty()) {
                        // No tasks available
                        Toast.makeText(TaskTrackerActivity.this, "No tasks available.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update RecyclerView with the list of tasks
                        taskAdapter.setTasks(tasks);
                    }

                } else {
                    // Handle known response codes like unauthorized or server error
                    int code = response.code();
                    if (code == 401) {
                        Toast.makeText(TaskTrackerActivity.this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
                    } else if (code == 500) {
                        Toast.makeText(TaskTrackerActivity.this, "Server error. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(TaskTrackerActivity.this, "Error " + code + ": " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                // Handle typical network issues or timeouts
                if (t.getMessage() != null && t.getMessage().toLowerCase().contains("timeout")) {
                    Toast.makeText(TaskTrackerActivity.this, "Request timed out. Please check your connection.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TaskTrackerActivity.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

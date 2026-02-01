package com.matias.disciteomnes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matias.disciteomnes.adapter.TaskAdapter;
import com.matias.disciteomnes.model.Task;
import com.matias.disciteomnes.model.TaskRequest;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity that displays and manages the group's study tasks
public class StudyPlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton addTaskButton;
    private DisciteOmnesApi api;

    private String token;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        recyclerView = findViewById(R.id.taskRecyclerView);
        addTaskButton = findViewById(R.id.addTaskButton);

        // Get JWT token and group ID from Intent
        token = getIntent().getStringExtra("token");
        groupId = getIntent().getStringExtra("groupId");

        api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);

        // Set up RecyclerView with task adapter
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load tasks from the backend
        loadTasks();

        // Show dialog to add a new task
        addTaskButton.setOnClickListener(view -> showAddTaskDialog());
    }

    // Load all tasks for the current group
    private void loadTasks() {
        api.getTasks("Bearer " + token, groupId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    taskAdapter.setTasks(response.body());
                } else {
                    Toast.makeText(StudyPlanActivity.this, "Error loading tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(StudyPlanActivity.this, "Network failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Display a dialog to input the title for a new task
    private void showAddTaskDialog() {
        EditText input = new EditText(this);
        input.setHint("Task title");

        new AlertDialog.Builder(this)
                .setTitle("New Task")
                .setMessage("Enter the task name")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        createTask(title);
                    } else {
                        Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Send request to backend to create a new task
    private void createTask(String title) {
        TaskRequest request = new TaskRequest();
        request.title = title;
        request.completed = false;

        api.addTask("Bearer " + token, groupId, request).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StudyPlanActivity.this, "Task created", Toast.LENGTH_SHORT).show();
                    loadTasks(); // Refresh task list
                } else {
                    Toast.makeText(StudyPlanActivity.this, "Error creating task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(StudyPlanActivity.this, "Network failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

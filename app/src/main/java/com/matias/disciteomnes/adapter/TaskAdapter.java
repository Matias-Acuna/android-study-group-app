package com.matias.disciteomnes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matias.disciteomnes.R;
import com.matias.disciteomnes.model.Task;
import com.matias.disciteomnes.model.TaskUpdateRequest;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Adapter class for displaying a list of tasks in a RecyclerView
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList = new ArrayList<>();

    // Update the task list and refresh the UI
    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the task item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Set task title and due date
        holder.taskTitle.setText(task.title);
        holder.taskDueDate.setText("Due: " + task.dueDate);

        // Disconnect checkbox listener temporarily to avoid unwanted triggers
        holder.taskCheckbox.setOnCheckedChangeListener(null);
        holder.taskCheckbox.setChecked(task.completed);

        // Reattach listener to handle user interaction with the checkbox
        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TaskUpdateRequest update = new TaskUpdateRequest();
            update.completed = isChecked;

            // Retrieve token from shared preferences
            String token = "Bearer " + holder.context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                    .getString("JWT_TOKEN", "");

            // Make API call to update task completion status
            DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
            api.updateTaskStatus(token, task.id, update).enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (!response.isSuccessful()) {
                        // Show error if update fails and revert checkbox state
                        Toast.makeText(holder.context, "Error updating task: " + response.code(), Toast.LENGTH_SHORT).show();
                        holder.taskCheckbox.setChecked(!isChecked);
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    // Handle network failure and revert checkbox state
                    Toast.makeText(holder.context, "Network failure: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    holder.taskCheckbox.setChecked(!isChecked);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // ViewHolder class to hold references to UI elements in the task item layout
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDueDate;
        CheckBox taskCheckbox;
        Context context;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
        }
    }
}

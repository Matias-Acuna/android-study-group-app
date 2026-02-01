package com.matias.disciteomnes.model;

// Request model used for creating or updating a task via API
public class TaskRequest {
    public String title;         // Title of the task
    public String description;   // Detailed description of the task
    public String dueDate;       // Due date in ISO format (e.g., YYYY-MM-DD)
    public String assignedTo;    // User ID of the person assigned to the task
    public boolean completed;    // Optional: whether the task is initially marked as completed
}

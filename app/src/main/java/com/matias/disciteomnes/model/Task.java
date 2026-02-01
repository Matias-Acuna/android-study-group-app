package com.matias.disciteomnes.model;

// Data model representing a task within a group
public class Task {
    public String id;             // Unique identifier of the task
    public String title;          // Title or short name of the task
    public String description;    // Detailed description of the task
    public String dueDate;        // Due date in ISO format (e.g., YYYY-MM-DD)
    public boolean completed;     // Indicates whether the task is completed
    public String assignedTo;     // User ID of the member assigned to this task
}

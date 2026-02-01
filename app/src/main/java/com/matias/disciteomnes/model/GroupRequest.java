package com.matias.disciteomnes.model;

// Request model used for creating a new group via API
public class GroupRequest {
    public String name;         // Name of the group to be created
    public String description;  // Description or purpose of the group

    // Constructor to initialize group creation parameters
    public GroupRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

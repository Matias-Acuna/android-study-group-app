package com.matias.disciteomnes.model;

import java.util.List;

// Data model representing a study group
public class Group {
    public String id;                    // Unique identifier of the group
    public String name;                  // Name of the group
    public String description;           // Description of the group's purpose or topic
    public List<String> members;         // List of user IDs who are members of this group
    public boolean isMember;             // Indicates whether the current user is a member of the group

    public String createdAt;             // Timestamp when the group was created (ISO 8601 format expected)
    public String createdBy;             // User ID of the group's creator
    public String createdByName;         // Name of the group's creator (optional, used for display)
}

package com.matias.disciteomnes.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matias.disciteomnes.R;
import com.matias.disciteomnes.StudyPlanActivity;
import com.matias.disciteomnes.model.ApiResponse;
import com.matias.disciteomnes.model.Group;
import com.matias.disciteomnes.network.DisciteOmnesApi;
import com.matias.disciteomnes.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Adapter class for displaying a list of study groups in a RecyclerView
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;
    private OnGroupClickListener listener;

    // Interface for handling group item clicks
    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    public GroupAdapter(List<Group> groupList, OnGroupClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    // Update the list of groups and refresh the view
    public void setGroups(List<Group> newGroups) {
        this.groupList = newGroups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the group item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        Context context = holder.itemView.getContext();

        // Retrieve the current user ID from shared preferences
        String currentUserId = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                .getString("USER_ID", "");

        // Populate the UI with group data
        holder.groupName.setText(group.name);
        holder.groupDescription.setText(group.description);
        holder.groupAuthor.setText("Author: " + (group.createdByName != null ? group.createdByName : "Unknown"));
        holder.groupCreatedAt.setText("Created on: " + (group.createdAt != null ? group.createdAt.substring(0, 10) : "N/A"));
        holder.groupParticipants.setText("Participants: " + (group.members != null ? group.members.size() : 0));

        // Configure join/leave buttons depending on membership status
        if (group.isMember) {
            holder.joinGroupButton.setText("Enter");
            holder.joinGroupButton.setVisibility(View.VISIBLE);
            holder.leaveGroupButton.setVisibility(View.VISIBLE);
        } else {
            holder.joinGroupButton.setText("Join group");
            holder.leaveGroupButton.setVisibility(View.GONE);
        }

        // Show delete button only if the current user is the creator of the group
        if (group.createdBy != null && group.createdBy.equals(currentUserId)) {
            holder.deleteGroupButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteGroupButton.setVisibility(View.GONE);
        }

        // Handle "join group" or "enter" button click
        holder.joinGroupButton.setOnClickListener(v -> {
            if (group.isMember) {
                // Open study plan activity if already a member
                Intent intent = new Intent(context, StudyPlanActivity.class);
                intent.putExtra("groupId", group.id);
                intent.putExtra("token", context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                        .getString("JWT_TOKEN", ""));
                context.startActivity(intent);
            } else {
                // Call join group API if not a member
                String token = "Bearer " + context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                        .getString("JWT_TOKEN", "");

                DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
                api.joinGroup(token, group.id).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "You have joined the group 🎉", Toast.LENGTH_SHORT).show();
                            group.isMember = true;

                            // Add current user to group member list if not already present
                            if (group.members != null && !group.members.contains(currentUserId)) {
                                group.members.add(currentUserId);
                            }

                            // Update participant count display
                            holder.groupParticipants.setText("Participants: " + group.members.size());
                            notifyItemChanged(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(context, "Network failure: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Handle leave group button click
        holder.leaveGroupButton.setOnClickListener(v -> {
            String token = "Bearer " + context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                    .getString("JWT_TOKEN", "");

            DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
            api.leaveGroup(token, group.id).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "You have left the group", Toast.LENGTH_SHORT).show();
                        group.isMember = false;

                        // Remove current user from group member list
                        if (group.members != null) {
                            group.members.remove(currentUserId);
                        }

                        // Update participant count display
                        holder.groupParticipants.setText("Participants: " + group.members.size());
                        notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(context, "Error leaving group: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Handle delete group button click (only available to group creator)
        holder.deleteGroupButton.setOnClickListener(v -> {
            String token = "Bearer " + context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                    .getString("JWT_TOKEN", "");

            DisciteOmnesApi api = RetrofitInstance.getInstance().create(DisciteOmnesApi.class);
            api.deleteGroup(token, group.id).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Group deleted", Toast.LENGTH_SHORT).show();
                        groupList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(context, "Error deleting group: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return groupList != null ? groupList.size() : 0;
    }

    // ViewHolder class to hold references to each UI component in the group item layout
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, groupDescription, groupAuthor, groupCreatedAt, groupParticipants;
        Button joinGroupButton, leaveGroupButton, deleteGroupButton;

        public GroupViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            groupDescription = itemView.findViewById(R.id.groupDescription);
            groupAuthor = itemView.findViewById(R.id.groupAuthor);
            groupCreatedAt = itemView.findViewById(R.id.groupCreatedAt);
            groupParticipants = itemView.findViewById(R.id.groupParticipants);
            joinGroupButton = itemView.findViewById(R.id.joinGroupButton);
            leaveGroupButton = itemView.findViewById(R.id.leaveGroupButton);
            deleteGroupButton = itemView.findViewById(R.id.deleteGroupButton);
        }
    }
}

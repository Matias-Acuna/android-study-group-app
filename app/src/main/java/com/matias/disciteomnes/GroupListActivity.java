package com.matias.disciteomnes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matias.disciteomnes.adapter.GroupAdapter;
import com.matias.disciteomnes.model.Group;
import com.matias.disciteomnes.network.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity that displays a list of all available groups for the user
public class GroupListActivity extends AppCompatActivity implements GroupAdapter.OnGroupClickListener {

    private RecyclerView recyclerGroups;
    private GroupAdapter groupAdapter;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        // Initialize RecyclerView and set layout manager
        recyclerGroups = findViewById(R.id.publicGroupsRecyclerView);
        recyclerGroups.setLayoutManager(new LinearLayoutManager(this));

        // Create adapter and set click listener
        groupAdapter = new GroupAdapter(new ArrayList<>(), this); // 👈 Listener passed
        recyclerGroups.setAdapter(groupAdapter);

        // Retrieve JWT token from shared preferences
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Token not found. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API to fetch list of groups
        RetrofitInstance.getApi().getGroups("Bearer " + token)
                .enqueue(new Callback<List<Group>>() {
                    @Override
                    public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                        if (response.isSuccessful()) {
                            // Populate the adapter with the list of groups
                            groupAdapter.setGroups(response.body());
                        } else {
                            Toast.makeText(GroupListActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Group>> call, Throwable t) {
                        Toast.makeText(GroupListActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 👇 Handle group item click event
    @Override
    public void onGroupClick(Group group) {
        // Open StudyPlanActivity when a group is selected
        Intent intent = new Intent(this, StudyPlanActivity.class);
        intent.putExtra("groupId", group.id);
        intent.putExtra("token", token);
        startActivity(intent);
    }
}

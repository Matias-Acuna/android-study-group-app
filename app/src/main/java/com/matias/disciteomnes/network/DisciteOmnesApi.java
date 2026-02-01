package com.matias.disciteomnes.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import com.matias.disciteomnes.model.RegisterRequest;
import com.matias.disciteomnes.model.LoginRequest;
import com.matias.disciteomnes.model.LoginResponse;
import com.matias.disciteomnes.model.ApiResponse;
import com.matias.disciteomnes.model.Group;
import com.matias.disciteomnes.model.GroupRequest;
import com.matias.disciteomnes.model.Task;
import com.matias.disciteomnes.model.TaskRequest;
import com.matias.disciteomnes.model.TaskUpdateRequest;

// Retrofit interface for defining API endpoints used by the app
public interface DisciteOmnesApi {

    // --- User Authentication ---

    // Register a new user
    @POST("auth/register")
    Call<ApiResponse> registerUser(@Body RegisterRequest registerRequest);

    // Login an existing user and retrieve a JWT token
    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // --- Group Management ---

    // Retrieve list of available groups
    @GET("groups")
    Call<List<Group>> getGroups(@Header("Authorization") String token);

    // Create a new group
    @POST("groups")
    Call<Group> createGroup(@Header("Authorization") String token, @Body GroupRequest groupRequest);

    // Join an existing group by ID
    @POST("groups/{groupId}/join")
    Call<ApiResponse> joinGroup(@Header("Authorization") String token, @Path("groupId") String groupId);

    // Leave a group by ID (custom endpoint)
    @POST("groups/{groupId}/leave") // ✅ New: leave group
    Call<ApiResponse> leaveGroup(@Header("Authorization") String token, @Path("groupId") String groupId);

    // Delete a group by ID (custom endpoint)
    @DELETE("groups/{groupId}") // ✅ New: delete group
    Call<ApiResponse> deleteGroup(@Header("Authorization") String token, @Path("groupId") String groupId);

    // --- Task Management ---

    // Get all tasks associated with a specific group
    @GET("groups/{groupId}/tasks")
    Call<List<Task>> getTasks(@Header("Authorization") String token, @Path("groupId") String groupId);

    // Add a new task to a specific group
    @POST("groups/{groupId}/tasks")
    Call<Task> addTask(@Header("Authorization") String token, @Path("groupId") String groupId, @Body TaskRequest taskRequest);

    // Update the completion status of a specific task
    @PUT("tasks/{taskId}")
    Call<Task> updateTaskStatus(@Header("Authorization") String token, @Path("taskId") String taskId, @Body TaskUpdateRequest updateRequest);
}

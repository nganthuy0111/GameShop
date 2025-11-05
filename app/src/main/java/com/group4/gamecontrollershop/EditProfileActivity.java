package com.group4.gamecontrollershop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;  // Import the Log class
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.User;
import android.content.SharedPreferences;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editFullName, editUsername, editAddress, editPhone;
    private Button saveButton;
    private DatabaseHelper databaseHelper;
    private String currentUserId; // We'll store the user ID from SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        editFullName = findViewById(R.id.editFullName);
        editUsername = findViewById(R.id.editUsername);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);
        saveButton = findViewById(R.id.saveButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("userId", null);

        if (currentUserId != null) {
            // Log current user details for testing
            Log.d("EditProfileActivity", "Current User ID: " + currentUserId);
            loadUserData(currentUserId);
        } else {
            Log.d("EditProfileActivity", "No user ID found in SharedPreferences.");
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is found
        }

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void loadUserData(String userId) {
        // Retrieve user data from the database
        User user = databaseHelper.getUserById(userId);
        if (user != null) {
            editFullName.setText(user.getFullname()); // Set the full name
            editUsername.setText(user.getUsername());
            editAddress.setText(user.getAddress());
            editPhone.setText(user.getPhone());
        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserProfile() {
        String fullName = editFullName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        if (currentUserId != null) {
            boolean isUpdated = databaseHelper.updateUserProfile(currentUserId, fullName, username, address, phone);
            if (isUpdated) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

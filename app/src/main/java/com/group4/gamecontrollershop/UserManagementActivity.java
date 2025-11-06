package com.group4.gamecontrollershop;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.adapter.UserAdminAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdminAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        databaseHelper = new DatabaseHelper(this);
        userList = new ArrayList<>();

        // Initialize views
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Check admin access
        if (!checkAdminAccess()) {
            Toast.makeText(this, "Access denied. Admin privileges required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdminAdapter(this, userList);
        recyclerViewUsers.setAdapter(adapter);

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());

        adapter.setOnItemClickListener(new UserAdminAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(User user) {
                showEditUserDialog(user);
            }

            @Override
            public void onDeleteClick(User user) {
                showDeleteConfirmDialog(user);
            }
        });

        loadUsers();
    }

    private boolean checkAdminAccess() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            return false;
        }

        User user = databaseHelper.getUserById(userId);
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    private void loadUsers() {
        userList = databaseHelper.getAllUsers();
        adapter.updateUserList(userList);
    }

    private void showEditUserDialog(User user) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_form, null);
        
        EditText etFullname = dialogView.findViewById(R.id.etUserFullname);
        EditText etUsername = dialogView.findViewById(R.id.etUserUsername);
        EditText etAddress = dialogView.findViewById(R.id.etUserAddress);
        EditText etPhone = dialogView.findViewById(R.id.etUserPhone);
        EditText etRole = dialogView.findViewById(R.id.etUserRole);

        // Fill with existing data
        etFullname.setText(user.getFullname() != null ? user.getFullname() : "");
        etUsername.setText(user.getUsername() != null ? user.getUsername() : "");
        etAddress.setText(user.getAddress() != null ? user.getAddress() : "");
        etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        etRole.setText(user.getRole() != null ? user.getRole() : "user");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit User")
                .setView(dialogView)
                .setPositiveButton("Update", (d, which) -> {
                    if (validateUserInput(dialogView)) {
                        String fullname = etFullname.getText().toString().trim();
                        String username = etUsername.getText().toString().trim();
                        String address = etAddress.getText().toString().trim();
                        String phone = etPhone.getText().toString().trim();
                        String role = etRole.getText().toString().trim().toLowerCase();

                        if (!role.equals("user") && !role.equals("admin")) {
                            Toast.makeText(this, "Role must be 'user' or 'admin'", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (databaseHelper.updateUser(user.getId(), fullname, username, address, phone, role)) {
                            Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showDeleteConfirmDialog(User user) {
        // Prevent deleting yourself
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", null);
        
        if (currentUserId != null && String.valueOf(user.getId()).equals(currentUserId)) {
            Toast.makeText(this, "You cannot delete your own account", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user \"" + 
                        (user.getFullname() != null && !user.getFullname().isEmpty() 
                                ? user.getFullname() 
                                : user.getUsername()) + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (databaseHelper.deleteUser(user.getId())) {
                        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateUserInput(View dialogView) {
        EditText etUsername = dialogView.findViewById(R.id.etUserUsername);
        EditText etRole = dialogView.findViewById(R.id.etUserRole);

        if (TextUtils.isEmpty(etUsername.getText())) {
            etUsername.setError("Username is required");
            return false;
        }
        if (TextUtils.isEmpty(etRole.getText())) {
            etRole.setError("Role is required");
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}


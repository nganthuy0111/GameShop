package com.group4.gamecontrollershop.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.LoginActivity;
import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.adapter.UserAdminAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdminUsers extends Fragment {

    private RecyclerView recyclerViewUsers;
    private UserAdminAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        userList = new ArrayList<>();

        // Initialize views
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        ImageButton btnLogout = view.findViewById(R.id.btnLogout);

        // Check admin access
        if (!checkAdminAccess()) {
            Toast.makeText(requireContext(), "Access denied. Admin privileges required.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Set logout button listener
        btnLogout.setOnClickListener(v -> handleLogout());

        // Setup RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserAdminAdapter(requireContext(), userList);
        recyclerViewUsers.setAdapter(adapter);

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
        return view;
    }

    private boolean checkAdminAccess() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
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
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_user_form, null);

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

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
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
                            Toast.makeText(requireContext(), "Role must be 'user' or 'admin'", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (databaseHelper.updateUser(user.getId(), fullname, username, address, phone, role)) {
                            Toast.makeText(requireContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update user", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showDeleteConfirmDialog(User user) {
        // Prevent deleting yourself
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", null);

        if (currentUserId != null && String.valueOf(user.getId()).equals(currentUserId)) {
            Toast.makeText(requireContext(), "You cannot delete your own account", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user \"" +
                        (user.getFullname() != null && !user.getFullname().isEmpty()
                                ? user.getFullname()
                                : user.getUsername()) + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (databaseHelper.deleteUser(user.getId())) {
                        Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
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

    private void handleLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear SharedPreferences
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Navigate to LoginActivity
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();

                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            loadUsers();
        }
    }
}


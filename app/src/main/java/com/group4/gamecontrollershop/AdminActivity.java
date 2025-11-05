package com.group4.gamecontrollershop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.User;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView tvTotalUsers, tvTotalProducts;
    private CardView cardManageProducts, cardManageUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        cardManageProducts = findViewById(R.id.cardManageProducts);
        cardManageUsers = findViewById(R.id.cardManageUsers);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Check if user is admin
        if (!checkAdminAccess()) {
            Toast.makeText(this, "Access denied. Admin privileges required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load statistics
        loadStatistics();

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());

        cardManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ProductManagementActivity.class);
            startActivity(intent);
        });

        cardManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });
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

    private void loadStatistics() {
        // Load total users
        int totalUsers = databaseHelper.getAllUsers().size();
        tvTotalUsers.setText(String.valueOf(totalUsers));

        // Load total products
        int totalProducts = databaseHelper.getAllProducts().size();
        tvTotalProducts.setText(String.valueOf(totalProducts));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh statistics when returning to this activity
        loadStatistics();
    }
}


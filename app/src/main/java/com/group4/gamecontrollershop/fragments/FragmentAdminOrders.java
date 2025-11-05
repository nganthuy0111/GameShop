package com.group4.gamecontrollershop.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.LoginActivity;
import com.group4.gamecontrollershop.OrderDetailActivity;
import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.adapter.HistoryAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.Order;
import com.group4.gamecontrollershop.model.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdminOrders extends Fragment {

    private RecyclerView recyclerViewOrders;
    private HistoryAdapter historyAdapter;
    private DatabaseHelper databaseHelper;
    private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_orders, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        orderList = new ArrayList<>();

        // Initialize views
        recyclerViewOrders = view.findViewById(R.id.recycleView);
        ImageButton btnLogout = view.findViewById(R.id.btnLogout);

        // Check admin access
        if (!checkAdminAccess()) {
            Toast.makeText(requireContext(), "Access denied. Admin privileges required.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Set logout button listener
        btnLogout.setOnClickListener(v -> handleLogout());

        // Setup RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyAdapter = new HistoryAdapter(orderList, requireContext());
        recyclerViewOrders.setAdapter(historyAdapter);

        loadOrders();
        return view;
    }

    private boolean checkAdminAccess() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            return false;
        }

        User user = databaseHelper.getUserById(userId);
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    private void loadOrders() {
        orderList = databaseHelper.getAllOrders(); // Get all orders (admin)
        historyAdapter.updateOrderList(orderList);
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
        if (historyAdapter != null) {
            loadOrders();
        }
    }
}


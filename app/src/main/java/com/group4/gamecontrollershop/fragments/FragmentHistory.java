package com.group4.gamecontrollershop.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.LoginActivity;
import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.adapter.HistoryAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FragmentHistory extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<Order> orderList;
    private DatabaseHelper myDB;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        myDB = new DatabaseHelper(getContext());

        Date orderDate = null;
        try {
            // You might want to get a dynamic date instead
            orderDate = dateFormat.parse("2023-08-15");
        } catch (ParseException e) {
            e.printStackTrace();
            // Consider displaying an error message or fallback here
        }

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null); // Default is null if not found

        if (userId == null) {
            // Handle case where userId is not found
            // Redirect to login or show an alert dialog
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        // Fetch all orders for the current user
        orderList = myDB.getAllOrders(Integer.parseInt(userId));

//        // If no orders exist, insert sample orders (for testing purposes)
//        if (orderList.isEmpty()) {
//            final String SUCCESS_STATUS = "success";
//            final String FAILURE_STATUS = "failure";
//
//            myDB.insertOrder(Integer.parseInt(userId), 1500000d, orderDate, SUCCESS_STATUS);
//            myDB.insertOrder(Integer.parseInt(userId), 1500000d, orderDate, SUCCESS_STATUS);
//            myDB.insertOrder(Integer.parseInt(userId), 1500000d, orderDate, SUCCESS_STATUS);
//            myDB.insertOrder(Integer.parseInt(userId), 1500000d, orderDate, FAILURE_STATUS);
//            myDB.insertOrder(Integer.parseInt(userId), 1500000d, orderDate, SUCCESS_STATUS);
//
//            // Refresh order list after insertion
//            orderList = myDB.getAllOrders(Integer.parseInt(userId));
//        }

        // Set up RecyclerView
        historyAdapter = new HistoryAdapter(orderList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(historyAdapter);

        return view;


    }
    @Override
    public void onResume() {
        super.onResume();
        reloadOrderList();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter("com.group4.gamecontrollershop.ORDER_PLACED");
        getContext().registerReceiver(orderPlacedReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(orderPlacedReceiver);
    }

    private final BroadcastReceiver orderPlacedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadOrderList(); // Refresh order data when broadcast is received
        }
    };

    private void reloadOrderList() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            orderList = myDB.getAllOrders(Integer.parseInt(userId));
            historyAdapter.updateOrderList(orderList);
        }
    }


}

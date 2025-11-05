package com.group4.gamecontrollershop;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.adapter.OrderDetailAdapter;
import com.group4.gamecontrollershop.model.OrderDetail;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView totalAmountText;
    private TextView orderDateText;
    private TextView userFullNameText;
    private TextView userAddressText;
    private TextView userPhoneText;
    private TextView orderStatusText;
    private ImageView orderStatusImage;
    private RecyclerView orderProductsRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Bind views
        TextView orderIdText = findViewById(R.id.orderId);
        totalAmountText = findViewById(R.id.orderTotal);
        orderDateText = findViewById(R.id.orderDate);
        userFullNameText = findViewById(R.id.userFullName);
        userAddressText = findViewById(R.id.userAddress);
        userPhoneText = findViewById(R.id.userPhone);
        orderStatusImage = findViewById(R.id.orderStatus);
        orderStatusText = findViewById(R.id.orderStatusText);
        orderProductsRecyclerView = findViewById(R.id.orderProductsRecyclerView);

        // Get data from the intent
        int orderId = getIntent().getIntExtra("orderId", -1);
        double totalAmount = getIntent().getDoubleExtra("orderTotalAmount", 0.0);
        String orderDate = getIntent().getStringExtra("orderDate");
        String orderStatus = getIntent().getStringExtra("orderStatus");

        // User info
        String userFullName = getIntent().getStringExtra("userFullName");
        String userAddress = getIntent().getStringExtra("userAddress");
        String userPhone = getIntent().getStringExtra("userPhone");

        // Set the data to the views
        orderIdText.setText("#" + orderId);
        totalAmountText.setText("$" + String.format("%.2f", totalAmount));
        orderDateText.setText(orderDate != null ? orderDate : "N/A");
        userFullNameText.setText(userFullName != null ? userFullName : "N/A");
        userAddressText.setText(userAddress != null ? userAddress : "N/A");
        userPhoneText.setText(userPhone != null ? userPhone : "N/A");

        // Set order status
        if ("success".equals(orderStatus)) {
            orderStatusImage.setImageResource(R.drawable.success);
            orderStatusText.setText("Success");
            orderStatusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
        } else {
            orderStatusImage.setImageResource(R.drawable.failure);
            orderStatusText.setText("Failed");
            orderStatusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        }

        // Retrieve order details
        @SuppressWarnings("unchecked")
        List<OrderDetail> orderDetails = (List<OrderDetail>) getIntent().getSerializableExtra("orderDetails");
        if (orderDetails != null) {
            setupRecyclerView(orderDetails);
        }
    }

    private void setupRecyclerView(List<OrderDetail> orderDetails) {
        orderProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderDetailAdapter adapter = new OrderDetailAdapter(orderDetails);
        orderProductsRecyclerView.setAdapter(adapter);
    }
}

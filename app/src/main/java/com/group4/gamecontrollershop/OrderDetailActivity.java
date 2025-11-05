package com.group4.gamecontrollershop;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.adapter.OrderDetailAdapter;
import com.group4.gamecontrollershop.model.OrderDetail;
import com.group4.gamecontrollershop.model.Product;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView totalAmountText;
    private TextView orderDateText;
    private TextView userFullNameText;
    private TextView userAddressText;
    private TextView userPhoneText;
    private TextView userEmailText;
    private TextView orderStatusText;
    private ImageView orderStatusImage;
    private RecyclerView orderProductsRecyclerView; // RecyclerView to display order product details

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Bind views
        TextView orderIdText = findViewById(R.id.orderId);
        totalAmountText = findViewById(R.id.orderTotal);
        orderDateText = findViewById(R.id.orderDate);
        userFullNameText = findViewById(R.id.userFullName);
        userAddressText = findViewById(R.id.userAddress);
        userPhoneText = findViewById(R.id.userPhone);
        orderStatusImage = findViewById(R.id.orderStatus);
        orderStatusText = findViewById(R.id.orderStatusText);
        orderProductsRecyclerView = findViewById(R.id.orderProductsRecyclerView); // Initialize RecyclerView

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
        orderIdText.setText("Order ID: #" + orderId);
        totalAmountText.setText("Total Amount: $" + totalAmount);
        orderDateText.setText("Order Date: " + orderDate);
        userFullNameText.setText("Full Name: " + (userFullName != null ? userFullName : "N/A"));
        userAddressText.setText("Address: " + (userAddress != null ? userAddress : "N/A"));
        userPhoneText.setText("Phone: " + (userPhone != null ? userPhone : "N/A"));


        // Set order status
        orderStatusText.setText("Status: " + orderStatus);
        if ("success".equals(orderStatus)) {
            orderStatusImage.setImageResource(R.drawable.success);
        } else {
            orderStatusImage.setImageResource(R.drawable.failure);
        }

        // Retrieve order details
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

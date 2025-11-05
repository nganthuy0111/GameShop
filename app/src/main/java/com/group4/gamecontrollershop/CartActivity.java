package com.group4.gamecontrollershop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.adapter.ProductCartAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.CartItem;
import com.group4.gamecontrollershop.model.Order;
import com.group4.gamecontrollershop.model.OrderDetail;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductCartAdapter productCartAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private TextView tvTotalPrice;
    private ImageView ivBack;
    private static final int PAYPAL_REQUEST_CODE = 123;
    private DatabaseHelper myDB;
    private int userId;
    private Double totalAmount;

    // PayPal Configuration
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("AXIwvWt-qyVQPaXC-fwEu9iRJqXzgDgRiEvCBN1qs9ktvpGRVHvxtYW5xj3SEmcJMno1xi3sJc15dOZf");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);

        myDB = new DatabaseHelper(this);
        if (myDB == null) {
            Log.e("CartActivity", "DatabaseHelper is not initialized.");
            return;
        }

        // Add PayPal intent
        Intent intents = new Intent(this, PayPalService.class);
        intents.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intents);

        // Initialize views
        recyclerView = findViewById(R.id.recycleView);
        tvTotalPrice = findViewById(R.id.tvProductPrice);
        ivBack = findViewById(R.id.ivBack);

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = Integer.parseInt(sharedPreferences.getString("userId", "1")); // Default is 1 if not found

        // Load cart items from the database
        cartItemList = myDB.getCartItems(userId);
        productCartAdapter = new ProductCartAdapter(cartItemList, myDB);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productCartAdapter);

        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        productCartAdapter.setOnItemClickListener(new ProductCartAdapter.OnItemClickListener() {
            @Override
            public void onQuantityChanged(int position) {
                updateTotalPrice();
                CartItem cartItem = cartItemList.get(position);
                myDB.updateCartItem(cartItem.getId(), cartItem.getQuantity());
            }

            @Override
            public void onItemRemoved(int position) {
                updateTotalPrice();
                CartItem cartItem = cartItemList.get(position);
                myDB.deleteCartItem(userId, cartItem.getProductId());
            }
        });

        findViewById(R.id.btnAddToCart).setOnClickListener(v -> processPayment());
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem cartItem: cartItemList) {
            totalPrice += cartItem.getProduct().getNewPrice() * cartItem.getQuantity();
        }
        tvTotalPrice.setText("$" + String.format("%.2f", totalPrice));
    }

    private void processPayment() {
        double amount = Double.parseDouble(tvTotalPrice.getText().toString().replace("$", ""));
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
                "Game Controller", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            String orderDate = dateFormat.format(new Date());

            if (resultCode == Activity.RESULT_OK && data != null) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("PaymentDetails", paymentDetails);

                        // Parse payment details
                        JSONObject jsonDetails = new JSONObject(paymentDetails);
                        String paymentState = jsonDetails.getJSONObject("response").getString("state");

                        // Retrieve user ID from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        userId = Integer.parseInt(sharedPreferences.getString("userId", "1"));

                        // Get total amount
                        String price = tvTotalPrice.getText().toString().replace("$", "");
                        totalAmount = Double.parseDouble(price);

                        // Prepare OrderDetail list
                        List<OrderDetail> orderDetails = new ArrayList<>();
                        for (CartItem cartItem : cartItemList) {
                            orderDetails.add(new OrderDetail(
                                    0, // Placeholder for ID
                                    0, // Placeholder for orderId, generated post insertion
                                    userId,
                                    cartItem.getProductId(),
                                    cartItem.getQuantity(),
                                    cartItem.getProduct().getNewPrice(),
                                    "123 Main St, Anytown, USA", // Placeholder address
                                    "1234567890", // Placeholder phone
                                    "johndoe@example.com", // Placeholder email
                                    cartItem.getProduct().getImgUrl(), // Add image URL from the product
                                    cartItem.getProduct().getName() // Add product name from the product
                            ));
                        }

                        // Insert order with order details
                        myDB.insertOrder(userId, totalAmount, orderDate, "success", orderDetails);

                        // Reduce product quantities in the database
                        for (CartItem cartItem : cartItemList) {
                            int newQuantity = cartItem.getProduct().getQuantity() - cartItem.getQuantity();
                            myDB.updateProductQuantity(cartItem.getProductId(), newQuantity);
                        }

                        // Clear cart items in the database and locally after successful order
                        for (CartItem cartItem : cartItemList) {
                            myDB.deleteCartItem(userId, cartItem.getProductId());
                        }
                        cartItemList.clear(); // Clear the local cart list
                        productCartAdapter.notifyDataSetChanged(); // Refresh cart RecyclerView

                        Intent intent = new Intent("com.group4.gamecontrollershop.ORDER_PLACED");
                        sendBroadcast(intent);

                        // Update total price display
                        updateTotalPrice();
                    } catch (Exception e) {
                        Log.e("PaymentError", "Error processing payment: " + e.getMessage());
                    }
                }
            } else {
                // Handle payment cancellation or failure
                String status = (resultCode == Activity.RESULT_CANCELED) ? "failure" : "invalid";
                Log.i("Payment", resultCode == Activity.RESULT_CANCELED ? "User cancelled the payment." : "Invalid payment or PayPalConfiguration submitted.");
                myDB.insertOrder(userId, totalAmount, orderDate, status, new ArrayList<>());
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}

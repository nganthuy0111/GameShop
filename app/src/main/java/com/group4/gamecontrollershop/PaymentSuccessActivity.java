package com.group4.gamecontrollershop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView tvOrderId;
    private TextView tvAmount;
    private Button btnHome;
    private Button btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Initialize views
        tvOrderId = findViewById(R.id.tvOrderId);
        tvAmount = findViewById(R.id.tvAmount);
        btnHome = findViewById(R.id.btnHome);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        // Get order details from intent
        String orderId = getIntent().getStringExtra("orderId");
        String amount = getIntent().getStringExtra("amount");

        // Display order information
        if (orderId != null) {
            tvOrderId.setText("Order ID: #" + orderId);
        }

        if (amount != null) {
            tvAmount.setText("Amount: $" + amount);
        }

        // Home button - go to MainActivity
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Continue Shopping button - go to MainActivity (Home tab)
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to cart after payment
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

package com.group4.gamecontrollershop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.Order;
import com.group4.gamecontrollershop.OrderDetailActivity;
import com.group4.gamecontrollershop.model.OrderDetail;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private static final String ORDER_SUCCESS = "success";
    private List<Order> orderList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Context context; // To start the new activity

    public HistoryAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order, position);

        // Set an onClickListener to the entire order item
        holder.itemView.setOnClickListener(v -> {
            int userId = order.getUserId(); // Get userId from the order
            DatabaseHelper db = new DatabaseHelper(context);

            // Fetch user details based on userId
            String userFullName = db.getUserFullName(userId);
            String userAddress = db.getUserAddress(userId);
            String userPhone = db.getUserPhone(userId);


            // Open the order detail activity and pass the order and user details
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            intent.putExtra("orderTotalAmount", order.getTotalAmount());
            intent.putExtra("orderDate", dateFormat.format(order.getOrderDate()));
            intent.putExtra("orderStatus", order.getStatus());

            // Pass user info to the intent
            intent.putExtra("userFullName", userFullName);
            intent.putExtra("userAddress", userAddress);
            intent.putExtra("userPhone", userPhone);

            // Pass the list of order details (ensure OrderDetail implements Serializable)
            intent.putExtra("orderDetails", (Serializable) order.getOrderDetails());


            context.startActivity(intent);
        });
    }


    public void updateOrderList(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView orderStatus;
        private TextView orderId, orderPrice, orderDate, productSummary;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderIdText);
            orderPrice = itemView.findViewById(R.id.totalAmountText);
            orderDate = itemView.findViewById(R.id.orderDateText);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            productSummary = itemView.findViewById(R.id.productSummaryText); // New TextView for product summary
        }

//        @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
//        public void bind(Order order) {
//            orderId.setText("Order: #" + order.getId());
//            orderPrice.setText("Amount: $" + order.getTotalAmount());
//            orderDate.setText("Order at " + dateFormat.format(order.getOrderDate()));
//
//            // Set product summary
//            StringBuilder summaryBuilder = new StringBuilder("Products: ");
//            List<OrderDetail> orderDetails = order.getOrderDetails();
//            summaryBuilder.append(orderDetails.size()).append(" item(s)");
//            productSummary.setText(summaryBuilder.toString());
//
//            // Set the order status image
//            if (ORDER_SUCCESS.equals(order.getStatus())) {
//                orderStatus.setImageResource(R.drawable.success);
//            } else {
//                orderStatus.setImageResource(R.drawable.failure);
//            }
//        }

        @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
        public void bind(Order order, int position) {
            // Set the order's sequential number instead of ID
            orderId.setText("Order: #" + (position + 1)); // Display order's sequential number

            // Other binding code remains the same
            orderPrice.setText("Amount: $" + order.getTotalAmount());
            orderDate.setText("Order at " + dateFormat.format(order.getOrderDate()));

            // Set product summary
            StringBuilder summaryBuilder = new StringBuilder("Products: ");
            List<OrderDetail> orderDetails = order.getOrderDetails();
            summaryBuilder.append(orderDetails.size()).append(" item(s)");
            productSummary.setText(summaryBuilder.toString());

            // Set the order status image
            if (ORDER_SUCCESS.equals(order.getStatus())) {
                orderStatus.setImageResource(R.drawable.success);
            } else {
                orderStatus.setImageResource(R.drawable.failure);
            }
        }
    }
}

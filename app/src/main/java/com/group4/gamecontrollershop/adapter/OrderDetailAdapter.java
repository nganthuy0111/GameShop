package com.group4.gamecontrollershop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.model.OrderDetail;
import com.bumptech.glide.Glide;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private List<OrderDetail> orderDetailList;

    public OrderDetailAdapter(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_item, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);

        // Set product name using the actual product name
        holder.productName.setText(orderDetail.getProductName()); // Update this line to get the product name
        holder.productQuantity.setText("Quantity: " + orderDetail.getQuantity());
        holder.productPrice.setText("$" + orderDetail.getPrice());

        // Load the product image using the image URL from the orderDetail
        String imageUrl = orderDetail.getImageUrl(); // Get the image URL from orderDetail
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productQuantity;
        TextView productPrice;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }
}

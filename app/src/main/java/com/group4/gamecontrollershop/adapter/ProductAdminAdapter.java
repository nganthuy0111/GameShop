package com.group4.gamecontrollershop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.model.Product;

import java.util.List;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductAdminAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity, productStatus;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productStatus = itemView.findViewById(R.id.productStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(productList.get(position));
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(productList.get(position));
                    }
                }
            });
        }

        void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText("$" + String.format("%.2f", product.getNewPrice()));
            productQuantity.setText("Qty: " + product.getQuantity());
            productStatus.setText(product.getStatus());
            
            if ("ACTIVE".equals(product.getStatus())) {
                productStatus.setTextColor(context.getColor(android.R.color.holo_green_dark));
            } else {
                productStatus.setTextColor(context.getColor(android.R.color.holo_red_dark));
            }

            Glide.with(context)
                    .load(product.getImgUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(productImage);
        }
    }
}


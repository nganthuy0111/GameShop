package com.group4.gamecontrollershop.adapter;

import android.annotation.SuppressLint;
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
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.CartItem;

import java.util.List;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ProductViewHolder> {

    private List<CartItem> cartItemList;
    private OnItemClickListener listener;

    private DatabaseHelper myDB;

    public ProductCartAdapter(List<CartItem> cartItemList, DatabaseHelper databaseHelper) {
        this.cartItemList = cartItemList;
        this.myDB = databaseHelper;
    }

    public interface OnItemClickListener {
        void onQuantityChanged(int position);
        void onItemRemoved(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName, productPrice, productQuantity;
        private Button btnPlus, btnMinus, btnRemove;



        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnRemove = itemView.findViewById(R.id.btnRemove);

            btnPlus.setOnClickListener(v -> {
                int position = getAdapterPosition();


                if (position != RecyclerView.NO_POSITION) {
                    CartItem cartItem = cartItemList.get(position);
                    // Check if the current quantity is less than the max quantity
                    if (cartItem.getQuantity() <  myDB.getProductQuantity(cartItem.getProductId())) {
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                        productQuantity.setText(String.valueOf(cartItem.getQuantity()));
                        if (listener != null) {
                            listener.onQuantityChanged(position);
                        }
                    }
                }
            });


            btnMinus.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    CartItem cartItem = cartItemList.get(position);
                    if (cartItem.getQuantity() > 1) {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        productQuantity.setText(String.valueOf(cartItem.getQuantity()));
                        if (listener != null) {
                            listener.onQuantityChanged(position);
                        }
                    }
                }
            });

            btnRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (listener != null) {
                        listener.onItemRemoved(position);
                    }
                    cartItemList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartItemList.size());
                }
            });
        }

        @SuppressLint({"SetTextI18n", "CheckResult"})
        public void bind(CartItem cartItem) {
            if (cartItem.getProduct() == null) return;

            productName.setText(cartItem.getProduct().getName());
            productPrice.setText("$" + cartItem.getProduct().getNewPrice());
            productQuantity.setText(String.valueOf(cartItem.getQuantity()));
            Glide.with(itemView.getContext())
                    .load(cartItem.getProduct().getImgUrl())
                    .into(productImage);
        }
    }
}


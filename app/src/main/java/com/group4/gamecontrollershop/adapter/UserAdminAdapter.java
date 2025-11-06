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
import com.group4.gamecontrollershop.model.User;

import java.util.List;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
    }

    public UserAdminAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateUserList(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName, userEmail, userRole;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            userRole = itemView.findViewById(R.id.userRole);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(userList.get(position));
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(userList.get(position));
                    }
                }
            });
        }

        void bind(User user) {
            String displayName = user.getFullname() != null && !user.getFullname().isEmpty() 
                    ? user.getFullname() 
                    : (user.getUsername() != null ? user.getUsername() : "Unknown");
            userName.setText(displayName);
            userEmail.setText(user.getUsername() != null ? user.getUsername() : "");
            userRole.setText(user.getRole() != null ? user.getRole().toUpperCase() : "USER");

            // Set role color
            if ("admin".equalsIgnoreCase(user.getRole())) {
                userRole.setTextColor(context.getColor(android.R.color.holo_red_dark));
            } else {
                userRole.setTextColor(context.getColor(android.R.color.holo_blue_dark));
            }

            // Load avatar
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(context)
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.ic_profile)
                        .into(userAvatar);
            } else {
                userAvatar.setImageResource(R.drawable.ic_profile);
            }
        }
    }
}


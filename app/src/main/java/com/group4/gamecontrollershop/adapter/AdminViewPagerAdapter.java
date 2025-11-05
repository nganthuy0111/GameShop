package com.group4.gamecontrollershop.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group4.gamecontrollershop.fragments.FragmentAdminOrders;
import com.group4.gamecontrollershop.fragments.FragmentAdminProducts;
import com.group4.gamecontrollershop.fragments.FragmentAdminUsers;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentAdminProducts();
            case 1:
                return new FragmentAdminUsers();
            case 2:
                return new FragmentAdminOrders();
            default:
                return new FragmentAdminProducts();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}


package com.group4.gamecontrollershop.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group4.gamecontrollershop.fragments.FragmentHistory;
import com.group4.gamecontrollershop.fragments.FragmentHome;
import com.group4.gamecontrollershop.fragments.FragmentProfile;
import com.group4.gamecontrollershop.fragments.FragmentSearch;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 : return new FragmentHome();
            case 1 : return new FragmentSearch();
            case 2 : return new FragmentHistory();
            case 3 : return new FragmentProfile();
            default: return new FragmentHome();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
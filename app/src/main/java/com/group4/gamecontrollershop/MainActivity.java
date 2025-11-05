package com.group4.gamecontrollershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.group4.gamecontrollershop.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        navigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.getMenu().findItem(R.id.mHome).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.mSearch).setChecked(true);
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.mHistory).setChecked(true);
                        break;
                    case 3:
                        navigationView.getMenu().findItem(R.id.mProfile).setChecked(true);
                        break;
                }
            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.mHome) {
                    viewPager.setCurrentItem(0);
                } else if (itemId == R.id.mSearch) {
                    viewPager.setCurrentItem(1);
                } else if (itemId == R.id.mHistory) {
                    viewPager.setCurrentItem(2);
                } else if (itemId == R.id.mProfile) {
                    viewPager.setCurrentItem(3);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.favorite_action) {
            openFavorite();
            return true;
        } else if (itemId == R.id.action_cart) {
            openCart();
            return true;
        } else if (itemId == R.id.action_notifications) {
            openNotifications();
            return true;
        } else if (itemId == R.id.action_location) {
            openLocation();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void openFavorite() {
        Intent intent = new Intent(this, FavoriteActivity.class);
        startActivity(intent);
        finish();
    }

    private void openCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
        finish();
    }

    private void openNotifications() {
        // Mở Activity thông báo
    }

    private void openLocation() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
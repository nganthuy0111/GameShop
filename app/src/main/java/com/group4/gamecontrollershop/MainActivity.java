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
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.group4.gamecontrollershop.adapter.AdminViewPagerAdapter;
import com.group4.gamecontrollershop.adapter.ViewPagerAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.User;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private ViewPager2 viewPager;
    private DatabaseHelper databaseHelper;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Ensure default admin user exists
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.ensureAdminUserExists();
        
        // Check if user is admin
        checkUserRole();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        navigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        
        // Find AppBarLayout to hide/show based on role
        com.google.android.material.appbar.AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);

        if (isAdmin) {
            // Hide toolbar for admin
            if (appBarLayout != null) {
                appBarLayout.setVisibility(android.view.View.GONE);
            }
            // Adjust ViewPager2 constraint to start from top when AppBarLayout is hidden
            ConstraintLayout.LayoutParams params = 
                (ConstraintLayout.LayoutParams) viewPager.getLayoutParams();
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topToBottom = ConstraintLayout.LayoutParams.UNSET;
            viewPager.setLayoutParams(params);
            
            // Admin view: Products, Users, Orders
            setupAdminView();
        } else {
            // Show toolbar for regular users
            if (appBarLayout != null) {
                appBarLayout.setVisibility(android.view.View.VISIBLE);
            }
            setSupportActionBar(toolbar);
            // Adjust ViewPager2 constraint to start below AppBarLayout
            // ViewPager2 already has correct constraint in XML (top_toBottomOf appBarLayout)
            
            // User view: Home, Search, History, Profile
            setupUserView();
        }
    }

    private void checkUserRole() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            User user = databaseHelper.getUserById(userId);
            isAdmin = user != null && "admin".equalsIgnoreCase(user.getRole());
        }
    }

    private void setupAdminView() {
        // Load admin menu
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.navigation_admin);
        
        // Setup AdminViewPagerAdapter
        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.getMenu().findItem(R.id.mAdminProducts).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.mAdminUsers).setChecked(true);
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.mAdminOrders).setChecked(true);
                        break;
                }
            }
        });

        navigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.mAdminProducts) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.mAdminUsers) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.mAdminOrders) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });
    }

    private void setupUserView() {
        // Load user menu
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.navigation);
        
        // Setup ViewPagerAdapter
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

        navigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.mHome) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.mSearch) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.mHistory) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (itemId == R.id.mProfile) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show toolbar menu for regular users (not admin)
        if (!isAdmin) {
            getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        }
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
        }
        else {
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
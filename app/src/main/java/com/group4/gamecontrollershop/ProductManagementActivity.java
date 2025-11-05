package com.group4.gamecontrollershop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.adapter.ProductAdminAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.Brand;
import com.group4.gamecontrollershop.model.Product;
import com.group4.gamecontrollershop.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductAdminAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        databaseHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();

        // Initialize views
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        // Check admin access
        if (!checkAdminAccess()) {
            Toast.makeText(this, "Access denied. Admin privileges required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerView
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdminAdapter(this, productList);
        recyclerViewProducts.setAdapter(adapter);

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());
        btnAddProduct.setOnClickListener(v -> showAddProductDialog());

        adapter.setOnItemClickListener(new ProductAdminAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Product product) {
                showEditProductDialog(product);
            }

            @Override
            public void onDeleteClick(Product product) {
                showDeleteConfirmDialog(product);
            }
        });

        loadProducts();
    }

    private boolean checkAdminAccess() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            return false;
        }

        User user = databaseHelper.getUserById(userId);
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    private void loadProducts() {
        productList = databaseHelper.getAllProducts();
        adapter.updateProductList(productList);
    }

    private void showAddProductDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null);
        
        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etDescription = dialogView.findViewById(R.id.etProductDescription);
        EditText etImgUrl = dialogView.findViewById(R.id.etProductImgUrl);
        EditText etDetailImgUrlFirst = dialogView.findViewById(R.id.etDetailImgUrlFirst);
        EditText etDetailImgUrlSecond = dialogView.findViewById(R.id.etDetailImgUrlSecond);
        EditText etDetailImgUrlThird = dialogView.findViewById(R.id.etDetailImgUrlThird);
        EditText etOldPrice = dialogView.findViewById(R.id.etProductOldPrice);
        EditText etNewPrice = dialogView.findViewById(R.id.etProductNewPrice);
        EditText etQuantity = dialogView.findViewById(R.id.etProductQuantity);
        Spinner spinnerBrand = dialogView.findViewById(R.id.spinnerBrand);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Load brands and setup spinner
        setupBrandSpinner(spinnerBrand, -1);
        // Setup status spinner
        setupStatusSpinner(spinnerStatus, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Product")
                .setView(dialogView)
                .setPositiveButton("Add", (d, which) -> {
                    if (validateProductInput(dialogView)) {
                        Product product = createProductFromDialog(dialogView, -1);
                        databaseHelper.insertProduct(product);
                        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showEditProductDialog(Product product) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null);
        
        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etDescription = dialogView.findViewById(R.id.etProductDescription);
        EditText etImgUrl = dialogView.findViewById(R.id.etProductImgUrl);
        EditText etDetailImgUrlFirst = dialogView.findViewById(R.id.etDetailImgUrlFirst);
        EditText etDetailImgUrlSecond = dialogView.findViewById(R.id.etDetailImgUrlSecond);
        EditText etDetailImgUrlThird = dialogView.findViewById(R.id.etDetailImgUrlThird);
        EditText etOldPrice = dialogView.findViewById(R.id.etProductOldPrice);
        EditText etNewPrice = dialogView.findViewById(R.id.etProductNewPrice);
        EditText etQuantity = dialogView.findViewById(R.id.etProductQuantity);
        Spinner spinnerBrand = dialogView.findViewById(R.id.spinnerBrand);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Fill with existing data
        etName.setText(product.getName());
        etDescription.setText(product.getDescription());
        etImgUrl.setText(product.getImgUrl());
        etDetailImgUrlFirst.setText(product.getDetailImgUrlFirst());
        etDetailImgUrlSecond.setText(product.getDetailImgUrlSecond());
        etDetailImgUrlThird.setText(product.getDetailImgUrlThird());
        etOldPrice.setText(String.valueOf(product.getOldPrice()));
        etNewPrice.setText(String.valueOf(product.getNewPrice()));
        etQuantity.setText(String.valueOf(product.getQuantity()));

        // Load brands and setup spinner with selected brand
        setupBrandSpinner(spinnerBrand, product.getBrandId());
        // Setup status spinner with selected status
        setupStatusSpinner(spinnerStatus, product.getStatus());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Product")
                .setView(dialogView)
                .setPositiveButton("Update", (d, which) -> {
                    if (validateProductInput(dialogView)) {
                        Product updatedProduct = createProductFromDialog(dialogView, product.getId());
                        if (databaseHelper.updateProduct(updatedProduct)) {
                            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                            loadProducts();
                        } else {
                            Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showDeleteConfirmDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (databaseHelper.deleteProduct(product.getId())) {
                        Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    } else {
                        Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateProductInput(View dialogView) {
        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etNewPrice = dialogView.findViewById(R.id.etProductNewPrice);
        EditText etQuantity = dialogView.findViewById(R.id.etProductQuantity);

        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Product name is required");
            return false;
        }
        if (TextUtils.isEmpty(etNewPrice.getText())) {
            etNewPrice.setError("Price is required");
            return false;
        }
        if (TextUtils.isEmpty(etQuantity.getText())) {
            etQuantity.setError("Quantity is required");
            return false;
        }
        return true;
    }

    private void setupBrandSpinner(Spinner spinnerBrand, int selectedBrandId) {
        List<Brand> brandList = databaseHelper.getActiveBrands();
        List<String> brandNames = new ArrayList<>();
        List<Integer> brandIds = new ArrayList<>();

        for (Brand brand : brandList) {
            brandNames.add(brand.getName());
            brandIds.add(brand.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, brandNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(adapter);

        // Set selected brand if editing
        if (selectedBrandId > 0) {
            int position = brandIds.indexOf(selectedBrandId);
            if (position >= 0) {
                spinnerBrand.setSelection(position);
            }
        }

        // Store brand IDs in tag for later retrieval
        spinnerBrand.setTag(brandIds);
    }

    private void setupStatusSpinner(Spinner spinnerStatus, String selectedStatus) {
        List<String> statusList = new ArrayList<>();
        statusList.add("ACTIVE");
        statusList.add("INACTIVE");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Set selected status if editing
        if (selectedStatus != null && !selectedStatus.isEmpty()) {
            int position = statusList.indexOf(selectedStatus.toUpperCase());
            if (position >= 0) {
                spinnerStatus.setSelection(position);
            } else {
                // Default to ACTIVE if status not found
                spinnerStatus.setSelection(0);
            }
        } else {
            // Default to ACTIVE for new products
            spinnerStatus.setSelection(0);
        }
    }

    private Product createProductFromDialog(View dialogView, int productId) {
        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etDescription = dialogView.findViewById(R.id.etProductDescription);
        EditText etImgUrl = dialogView.findViewById(R.id.etProductImgUrl);
        EditText etDetailImgUrlFirst = dialogView.findViewById(R.id.etDetailImgUrlFirst);
        EditText etDetailImgUrlSecond = dialogView.findViewById(R.id.etDetailImgUrlSecond);
        EditText etDetailImgUrlThird = dialogView.findViewById(R.id.etDetailImgUrlThird);
        EditText etOldPrice = dialogView.findViewById(R.id.etProductOldPrice);
        EditText etNewPrice = dialogView.findViewById(R.id.etProductNewPrice);
        EditText etQuantity = dialogView.findViewById(R.id.etProductQuantity);
        Spinner spinnerBrand = dialogView.findViewById(R.id.spinnerBrand);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        int id = productId > 0 ? productId : 0;
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String imgUrl = etImgUrl.getText().toString().trim();
        String detailImgUrlFirst = etDetailImgUrlFirst.getText().toString().trim();
        String detailImgUrlSecond = etDetailImgUrlSecond.getText().toString().trim();
        String detailImgUrlThird = etDetailImgUrlThird.getText().toString().trim();
        double oldPrice = TextUtils.isEmpty(etOldPrice.getText()) ? 0 : Double.parseDouble(etOldPrice.getText().toString());
        double newPrice = Double.parseDouble(etNewPrice.getText().toString());
        int quantity = Integer.parseInt(etQuantity.getText().toString());
        
        // Get brand ID from spinner
        @SuppressWarnings("unchecked")
        List<Integer> brandIds = (List<Integer>) spinnerBrand.getTag();
        int brandId = brandIds != null && spinnerBrand.getSelectedItemPosition() >= 0 
                ? brandIds.get(spinnerBrand.getSelectedItemPosition()) 
                : 1;
        
        // Get status from spinner
        String status = spinnerStatus.getSelectedItemPosition() >= 0 
                ? spinnerStatus.getSelectedItem().toString() 
                : "ACTIVE";

        Date releaseDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            releaseDate = dateFormat.parse(dateFormat.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Product product = new Product(id, name, description, imgUrl, detailImgUrlFirst, 
                detailImgUrlSecond, detailImgUrlThird, oldPrice, newPrice, quantity, releaseDate, status, brandId);
        return product;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}


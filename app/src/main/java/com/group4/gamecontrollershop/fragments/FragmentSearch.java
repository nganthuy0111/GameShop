package com.group4.gamecontrollershop.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.adapter.ProductAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.Product;

import java.lang.reflect.Field;
import java.util.List;

public class FragmentSearch extends Fragment {
    private RecyclerView productRV;
    private TextView resultSizeTxt;
    private ProductAdapter productAdapter;
    private DatabaseHelper myDB;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        productRV = view.findViewById(R.id.productRV);
        resultSizeTxt = view.findViewById(R.id.resultSizeTxt);
        productRV.setLayoutManager(new GridLayoutManager(getContext(), 2));

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
                myDB = new DatabaseHelper(getContext());
                loadProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý sự kiện khi nội dung tìm kiếm thay đổi
                return false;
            }
        });

        try {
            Field searchField = SearchView.class.getDeclaredField("mSearchSrcTextView");
            searchField.setAccessible(true);
            TextView searchTextView = (TextView) searchField.get(searchView);
            searchTextView.setTextColor(Color.BLACK);
            searchTextView.setHintTextColor(Color.GRAY);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return view;
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void loadProducts(String searchInput) {
        List<Product> productList = myDB.searchProductsByName(searchInput);
        resultSizeTxt.setText("Have" + " " + productList.size() + " " + "result");
        if (productAdapter == null) {
            productAdapter = new ProductAdapter(getContext(), productList);
            productRV.setAdapter(productAdapter);
        } else {
            productAdapter.updateProductList(productList);
            productAdapter.notifyDataSetChanged();
        }
    }
}

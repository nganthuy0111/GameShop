package com.group4.gamecontrollershop.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.adapter.BrandAdapter;
import com.group4.gamecontrollershop.adapter.ProductAdapter;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.model.Brand;
import com.group4.gamecontrollershop.model.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FragmentHome extends Fragment {
    private RecyclerView productRV;
    private RecyclerView brandRV;
    private ProductAdapter productAdapter;
    private BrandAdapter brandAdapter;
    private ImageButton btnSort;
    private DatabaseHelper myDB;
    private String sortStatus = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        productRV = view.findViewById(R.id.productRV);
        brandRV = view.findViewById(R.id.brandRV);
        btnSort = view.findViewById(R.id.btnSort);
        productRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
        brandRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        brandRV.setLayoutManager(layoutManager);
//        LinearSnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(brandRV);

        btnSort.setOnClickListener(v -> showSortMenu(v));

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date releaseDate = null;
        try {
            releaseDate = dateFormat.parse("2023-08-15");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Brand xbox = new Brand("Xbox");
        Brand eightBitDo = new Brand("8BitDo");
        Brand sony = new Brand("Sony");
        Brand nintendo = new Brand("Nintendo");
        Brand razer = new Brand("Razer");
        Brand logitech = new Brand("Logitech");
        Brand steelSeries = new Brand("SteelSeries");
        Brand astro = new Brand("Astro");
        Brand thrustmaster = new Brand("Thrustmaster");
        Brand hori = new Brand("Hori");

        myDB = new DatabaseHelper(getContext());

        List<Brand> brandList = myDB.getActiveBrands();

        if (brandList.isEmpty()) {
        myDB.insertBrand(xbox);
        myDB.insertBrand(sony);
        myDB.insertBrand(eightBitDo);
        myDB.insertBrand(nintendo);
        myDB.insertBrand(razer);
        myDB.insertBrand(logitech);
        myDB.insertBrand(steelSeries);
        myDB.insertBrand(astro);
        myDB.insertBrand(thrustmaster);
        myDB.insertBrand(hori);
        }
//        myDB.deleteAllBrands();

        List<Brand> brands = myDB.getActiveBrands();

        int xboxId = -1,
            eightBitDoId = -1,
            sonyId = -1,
            nintendoId = -1,
            razerId = -1,
            logitechId = -1,
            steelSeriesId = -1,
            astroId = -1,
            thrustmasterId = -1,
            horiId = -1;

        for (Brand brand : brands) {
            if (brand.getName().equalsIgnoreCase("Xbox")) {
                xboxId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("8BitDo")) {
                eightBitDoId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("Sony")) {
                sonyId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("Nintendo")) {
                nintendoId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("Razer")) {
                razerId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("Logitech")) {
                logitechId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("SteelSeries")) {
                steelSeriesId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("Astro")) {
                astroId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("Thrustmaster")) {
                thrustmasterId = brand.getId();
            } else if (brand.getName().equalsIgnoreCase("Hori")) {
                horiId = brand.getId();
            }
        }

        String xboxOneSWhiteUrl = "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1s_white.jpg?alt=media&token=0a338c00-af26-4732-877e-90df25693137";
        String xboxOneSBlackUrl = "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1s_black.jpg?alt=media&token=a4c1b9dc-ac3d-4e1f-be22-2fc249a25fda";
        String xboxOneXWhiteUrl = "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x_white.jpg?alt=media&token=6332adc9-9caf-447e-8c6d-84022fb7d077";
        String xboxOneXBackUrl = "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x_black.jpg?alt=media&token=256f043a-7eda-445a-9eb2-3c3cd28cd787";
        String xboxOneXGreyCamoUrl = "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x_grey_camo.jpg?alt=media&token=942f5c77-28fd-4bc9-a04b-779947176cfb";
        String xboxOneXBlueCamoUrl = "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x_blue_camo.jpg?alt=media&token=8f4bcb71-1f06-4b2f-877c-ca0015b2ab88";
        String xboxOneXRedCamoUrl = "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x_red_camo.jpg?alt=media&token=ab57a9c8-1621-4967-a3e0-e5e154ffbe15";

        Product xboxOneSWhite = new Product(
                "Xbox One S White Controller",
                "The Xbox One S Controller is an ergonomic game pad designed for Xbox One consoles and Windows PCs. It features Bluetooth connectivity, textured grips, and responsive buttons for a comfortable and precise gaming experience. Customizable settings through the Xbox Accessories app enhance personalization for gamers.",
                xboxOneSWhiteUrl,
                xboxOneSWhiteUrl,
                xboxOneSWhiteUrl,
                xboxOneSWhiteUrl,
                59.99,
                39.99,
                100,
                releaseDate,
                "ACTIVE",
                xboxId
        );

        Product xboxOneSBlack = new Product(
                "Xbox One S Black Controller",
                "The Xbox One S Controller is an ergonomic game pad designed for Xbox One consoles and Windows PCs. It features Bluetooth connectivity, textured grips, and responsive buttons for a comfortable and precise gaming experience. Customizable settings through the Xbox Accessories app enhance personalization for gamers.",
                xboxOneSBlackUrl,
                xboxOneSBlackUrl,
                xboxOneSBlackUrl,
                xboxOneSBlackUrl,
                59.99,
                29.99,
                100,
                releaseDate,
                "ACTIVE",
                eightBitDoId
        );

        Product xboxOneXWhite = new Product(
                "Xbox One X White Controller",
                "The Xbox One S Controller is an ergonomic game pad designed for Xbox One consoles and Windows PCs. It features Bluetooth connectivity, textured grips, and responsive buttons for a comfortable and precise gaming experience. Customizable settings through the Xbox Accessories app enhance personalization for gamers.",
                xboxOneXWhiteUrl,
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1xWhite2.jpg?alt=media&token=177af073-20bd-4eb1-a43e-77b63822ff96",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1xWhite3.jpg?alt=media&token=ec638c91-cd0a-4a14-b0a4-13fcc4942d57",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1xWhite4.jpg?alt=media&token=c2f2c573-9e9e-44f1-bd0e-d669e7338dff",
                59.99,
                19.99,
                100,
                releaseDate,
                "ACTIVE",
                sonyId
        );

        Product xboxOneXBlack = new Product(
                "Xbox One X Black Controller",
                "The Xbox One S Controller is an ergonomic game pad designed for Xbox One consoles and Windows PCs. It features Bluetooth connectivity, textured grips, and responsive buttons for a comfortable and precise gaming experience. Customizable settings through the Xbox Accessories app enhance personalization for gamers.",
                xboxOneXBackUrl,
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x2.jpg?alt=media&token=7d8d86fd-2244-42dc-8c30-9569511cbf21",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x3.jpg?alt=media&token=57c8ae4c-c09c-473f-a9a8-44a25a4b9ac5",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x4.jpg?alt=media&token=21a1ae15-b3d1-46c1-931c-444af93aeab8",
                59.99,
                9.99,
                100,
                releaseDate,
                "ACTIVE",
                nintendoId
        );

        Product xboxOneXGreyCamo = new Product(
                "Xbox One X Grey Camo Controller",
                "The Xbox One S Controller is an ergonomic game pad designed for Xbox One consoles and Windows PCs. It features Bluetooth connectivity, textured grips, and responsive buttons for a comfortable and precise gaming experience. Customizable settings through the Xbox Accessories app enhance personalization for gamers.",
                xboxOneXGreyCamoUrl,
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x2.jpg?alt=media&token=7d8d86fd-2244-42dc-8c30-9569511cbf21",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x3.jpg?alt=media&token=57c8ae4c-c09c-473f-a9a8-44a25a4b9ac5",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x4.jpg?alt=media&token=21a1ae15-b3d1-46c1-931c-444af93aeab8",
                59.99,
                8.99,
                100,
                releaseDate,
                "ACTIVE",
                xboxId
        );

        Product xboxOneXBlueCamo = new Product(
                "Xbox One X Blue Camo Controller",
                "The Xbox One S Controller is an ergonomic game pad designed for Xbox One consoles and Windows PCs. It features Bluetooth connectivity, textured grips, and responsive buttons for a comfortable and precise gaming experience. Customizable settings through the Xbox Accessories app enhance personalization for gamers.",
                xboxOneXBlueCamoUrl,
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x2.jpg?alt=media&token=7d8d86fd-2244-42dc-8c30-9569511cbf21",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x3.jpg?alt=media&token=57c8ae4c-c09c-473f-a9a8-44a25a4b9ac5",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x4.jpg?alt=media&token=21a1ae15-b3d1-46c1-931c-444af93aeab8",
                59.99,
                7.99,
                4,
                releaseDate,
                "ACTIVE",
                xboxId
        );

        Product xboxOneXRedCamo = new Product(
                "Xbox One X Red Camo Controller",
                "The Xbox One S Controller is an ergonomic game pad designed for Xbox One consoles and Windows PCs. It features Bluetooth connectivity, textured grips, and responsive buttons for a comfortable and precise gaming experience. Customizable settings through the Xbox Accessories app enhance personalization for gamers.",
                xboxOneXRedCamoUrl,
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x2.jpg?alt=media&token=7d8d86fd-2244-42dc-8c30-9569511cbf21",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x3.jpg?alt=media&token=57c8ae4c-c09c-473f-a9a8-44a25a4b9ac5",
                "https://firebasestorage.googleapis.com/v0/b/game-controller-shop-50da9.appspot.com/o/xbox1x4.jpg?alt=media&token=21a1ae15-b3d1-46c1-931c-444af93aeab8",
                59.99,
                7.99,
                3,
                releaseDate,
                "ACTIVE",
                xboxId
        );




        boolean isEmptyProducts = myDB.getActiveProductsBySort(sortStatus).isEmpty();
        if (isEmptyProducts) {
            myDB.insertProduct(xboxOneXWhite);
            myDB.insertProduct(xboxOneXBlack);
            myDB.insertProduct(xboxOneXBlueCamo);
            myDB.insertProduct(xboxOneXGreyCamo);
            myDB.insertProduct(xboxOneXRedCamo);
            myDB.insertProduct(xboxOneSBlack);
            myDB.insertProduct(xboxOneSWhite);
        }

//        myDB.deleteAllProducts();
        loadProducts();

        brandAdapter = new BrandAdapter(getContext(), brands, this::loadProductsByBrandId);
        brandRV.setAdapter(brandAdapter);

        return view;
    }

    private void showSortMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.getMenuInflater().inflate(R.menu.menu_sort, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.sort_by_price_descending) {
                Toast.makeText(getContext(), "Highest to lowest", Toast.LENGTH_SHORT).show();
                sortStatus = "DESC";
            } else if (item.getItemId() == R.id.sort_by_price_ascending) {
                Toast.makeText(getContext(), "Lowest to highest", Toast.LENGTH_SHORT).show();
                sortStatus = "ASC";
            } else if (item.getItemId() == R.id.sort_by_all) {
                Toast.makeText(getContext(), "All", Toast.LENGTH_SHORT).show();
                sortStatus = "ALL";
            } else {
                return false;
            }
            loadProducts();
            return true;
        });
        popup.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProducts() {
        List<Product> productList = myDB.getActiveProductsBySort(sortStatus);
        List<Brand> brandList = myDB.getActiveBrands();
        if (productAdapter == null) {
            productAdapter = new ProductAdapter(getContext(), productList);
            productRV.setAdapter(productAdapter);
        } else {
            productAdapter.updateProductList(productList);
            productAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProductsByBrandId(Brand brand) {
        List<Product> productList = myDB.getActiveProductsByBrandId(brand.getId());
        productAdapter.updateProductList(productList);
        productAdapter.notifyDataSetChanged();
    }
}

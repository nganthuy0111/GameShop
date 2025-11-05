package com.group4.gamecontrollershop.database_helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.group4.gamecontrollershop.model.Brand;
import com.group4.gamecontrollershop.model.CartItem;
import com.group4.gamecontrollershop.model.Favorite;
import com.group4.gamecontrollershop.model.Order;
import com.group4.gamecontrollershop.model.OrderDetail;
import com.group4.gamecontrollershop.model.Product;
import com.group4.gamecontrollershop.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game_controller.db";
    private static final int DATABASE_VERSION = 3;

    //product
    private static final String TABLE_PRODUCT = "Product";
    private static final String TABLE_FAVORITE = "Favorite";
    private static final String TABLE_CART = "Cart";
    private static final String PRODUCT_COLUMN_ID = "id";
    private static final String PRODUCT_COLUMN_NAME = "name";
    private static final String PRODUCT_COLUMN_DESCRIPTION = "description";
    private static final String PRODUCT_COLUMN_IMG_URL = "imgUrl";
    private static final String PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST = "detailImgUrlFirst";
    private static final String PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND = "detailImgUrlSecond";
    private static final String PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD = "detailImgUrlThird";
    private static final String PRODUCT_COLUMN_OLD_PRICE = "oldPrice";
    private static final String PRODUCT_COLUMN_NEW_PRICE = "newPrice";
    private static final String PRODUCT_COLUMN_QUANTITY = "quantity";
    private static final String PRODUCT_COLUMN_RELEASE_DATE = "releaseDate";
    private static final String PRODUCT_COLUMN_STATUS = "status";
    private static final String PRODUCT_COLUMN_BRAND_ID = "brandId";

    //brand
    private static final String TABLE_BRAND = "Brand";
    private static final String BRAND_COLUMN_ID = "id";
    private static final String BRAND_COLUMN_NAME = "name";


    private static final String CREATE_TABLE_USER = "CREATE TABLE User (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "fullname TEXT, " +
            "username TEXT, " +
            "password TEXT, " +
            "avatarUrl TEXT, " +
            "address TEXT, " +
            "googleId TEXT, " +
            "status TEXT, " +
            "phone TEXT);";

    private static final String CREATE_TABLE_BRAND = "CREATE TABLE Brand (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT);";

    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE Product (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "description TEXT, " +
            "imgUrl TEXT, " +
            "detailImgUrlFirst TEXT, " +
            "detailImgUrlSecond TEXT, " +
            "detailImgUrlThird TEXT, " +
            "oldPrice REAL, " +
            "newPrice REAL, " +
            "quantity INTEGER, " +
            "brandId INTEGER, " +
            "releaseDate TEXT, " +
            "status TEXT, " +
            "FOREIGN KEY(brandId) REFERENCES Brand(id));";

    private static final String CREATE_TABLE_ORDER = "CREATE TABLE `Order` (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userId INTEGER, " +
            "totalAmount REAL, " +
            "orderDate TEXT, " +
            "status TEXT, " +
            "FOREIGN KEY(userId) REFERENCES User(id));";


    private static final String CREATE_TABLE_ORDER_ITEM = "CREATE TABLE OrderItem (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "orderId INTEGER, " +
            "productId INTEGER, " +
            "quantity INTEGER, " +
            "price REAL, " +
            "FOREIGN KEY(orderId) REFERENCES `Order`(id), " +
            "FOREIGN KEY(productId) REFERENCES Product(id));";

    private static final String CREATE_TABLE_FAVORITE = "CREATE TABLE Favorite (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userId INTEGER, " +
            "productId INTEGER, " +
            "FOREIGN KEY(userId) REFERENCES User(id), " +
            "FOREIGN KEY(productId) REFERENCES Product(id));";

    private static final String CREATE_TABLE_CART = "CREATE TABLE Cart (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userId INTEGER, " +
            "productId INTEGER, " +
            "quantity INTEGER, " +
            "FOREIGN KEY(userId) REFERENCES User(id), " +
            "FOREIGN KEY(productId) REFERENCES Product(id));";

    private static final String CREATE_TABLE_ORDER_DETAIL = "CREATE TABLE OrderDetail (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "orderId INTEGER, " +
            "userId INTEGER, " +
            "productId INTEGER, " + //  track products in an order
            "quantity INTEGER, " +  //  track the number of units bought
            "price REAL, " +        //  store the price of each product
            "address TEXT, " +
            "phone TEXT, " +
            "email TEXT, " +
            "imageUrl TEXT,"+
            "productName TEXT,"+
            "FOREIGN KEY(orderId) REFERENCES `Order`(id), " +
            "FOREIGN KEY(userId) REFERENCES User(id), " +
            "FOREIGN KEY(productId) REFERENCES Product(id));";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_BRAND);
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TABLE_ORDER_DETAIL);
        db.execSQL(CREATE_TABLE_ORDER_ITEM);
        db.execSQL(CREATE_TABLE_FAVORITE);
        db.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS Favorite");
        db.execSQL("DROP TABLE IF EXISTS OrderItem");
        db.execSQL("DROP TABLE IF EXISTS `Order`");
        db.execSQL("DROP TABLE IF EXISTS Product");
        db.execSQL("DROP TABLE IF EXISTS Brand");
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Cart");
        db.execSQL("DROP TABLE IF EXISTS OrderDetail");
        // Tạo lại các bảng mới
        onCreate(db);

    }

    public void insertProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_COLUMN_NAME, product.getName());
        values.put(PRODUCT_COLUMN_DESCRIPTION, product.getDescription());
        values.put(PRODUCT_COLUMN_IMG_URL, product.getImgUrl());
        values.put(PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST, product.getDetailImgUrlFirst());
        values.put(PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND, product.getDetailImgUrlSecond());
        values.put(PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD, product.getDetailImgUrlThird());
        values.put(PRODUCT_COLUMN_OLD_PRICE, product.getOldPrice());
        values.put(PRODUCT_COLUMN_NEW_PRICE, product.getNewPrice());
        values.put(PRODUCT_COLUMN_QUANTITY, product.getQuantity());
        values.put(PRODUCT_COLUMN_BRAND_ID, product.getBrandId()); // Sử dụng brandId
        values.put(PRODUCT_COLUMN_STATUS, product.getStatus());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String releaseDateString = dateFormat.format(product.getReleaseDate());
        values.put(PRODUCT_COLUMN_RELEASE_DATE, releaseDateString);

        db.insert(TABLE_PRODUCT, null, values);
        db.close();
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{
                        PRODUCT_COLUMN_ID, PRODUCT_COLUMN_NAME, PRODUCT_COLUMN_DESCRIPTION, PRODUCT_COLUMN_IMG_URL,
                        PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST, PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND, PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD,
                        PRODUCT_COLUMN_OLD_PRICE, PRODUCT_COLUMN_NEW_PRICE, PRODUCT_COLUMN_QUANTITY,
                        PRODUCT_COLUMN_BRAND_ID, PRODUCT_COLUMN_RELEASE_DATE, PRODUCT_COLUMN_STATUS},
                PRODUCT_COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = null;
            try {
                releaseDate = dateFormat.parse(cursor.getString(11));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Product product = new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5), cursor.getString(6),
                    cursor.getDouble(7), cursor.getDouble(8), cursor.getInt(9), releaseDate, cursor.getString(12), cursor.getInt(10));
            cursor.close();
            return product;
        } else {
            return null;
        }
    }

    ///AAA
    public List<Product> getActiveProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Add quantity > 0 condition to the query
        String selection = PRODUCT_COLUMN_STATUS + "=? AND " + PRODUCT_COLUMN_QUANTITY + " > ?";
        String[] selectionArgs = new String[]{"ACTIVE", "0"};

        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{
                        PRODUCT_COLUMN_ID, PRODUCT_COLUMN_NAME, PRODUCT_COLUMN_DESCRIPTION, PRODUCT_COLUMN_IMG_URL,
                        PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST, PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND, PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD,
                        PRODUCT_COLUMN_OLD_PRICE, PRODUCT_COLUMN_NEW_PRICE, PRODUCT_COLUMN_QUANTITY,
                        PRODUCT_COLUMN_BRAND_ID, PRODUCT_COLUMN_RELEASE_DATE, PRODUCT_COLUMN_STATUS},
                selection, selectionArgs, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            do {
                Date releaseDate = null;
                try {
                    releaseDate = dateFormat.parse(cursor.getString(11));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Product product = new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6),
                        cursor.getDouble(7), cursor.getDouble(8), cursor.getInt(9), releaseDate, cursor.getString(12), cursor.getInt(10));
                productList.add(product);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return productList;
    }


    ///AAA
    public List<Product> getActiveProductsBySort(String sortOrder) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        // Add quantity > 0 check to the query
        String selection = PRODUCT_COLUMN_STATUS + "=? AND " + PRODUCT_COLUMN_QUANTITY + " > ?";
        String[] selectionArgs = new String[]{"ACTIVE", "0"};

        if (sortOrder.equals("ALL")) {
            cursor = db.query(TABLE_PRODUCT, new String[]{
                            PRODUCT_COLUMN_ID, PRODUCT_COLUMN_NAME, PRODUCT_COLUMN_DESCRIPTION, PRODUCT_COLUMN_IMG_URL,
                            PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST, PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND, PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD,
                            PRODUCT_COLUMN_OLD_PRICE, PRODUCT_COLUMN_NEW_PRICE, PRODUCT_COLUMN_QUANTITY, PRODUCT_COLUMN_BRAND_ID,
                            PRODUCT_COLUMN_RELEASE_DATE, PRODUCT_COLUMN_STATUS},
                    selection, selectionArgs, null, null, null, null);
        } else {
            String orderBy = PRODUCT_COLUMN_NEW_PRICE + " " + sortOrder;

            cursor = db.query(TABLE_PRODUCT, new String[]{
                            PRODUCT_COLUMN_ID, PRODUCT_COLUMN_NAME, PRODUCT_COLUMN_DESCRIPTION, PRODUCT_COLUMN_IMG_URL,
                            PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST, PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND, PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD,
                            PRODUCT_COLUMN_OLD_PRICE, PRODUCT_COLUMN_NEW_PRICE, PRODUCT_COLUMN_QUANTITY, PRODUCT_COLUMN_BRAND_ID,
                            PRODUCT_COLUMN_RELEASE_DATE, PRODUCT_COLUMN_STATUS},
                    selection, selectionArgs, null, null, orderBy, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            do {
                Date releaseDate = null;
                try {
                    releaseDate = dateFormat.parse(cursor.getString(11));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Product product = new Product(
                        cursor.getInt(0), // id
                        cursor.getString(1), // name
                        cursor.getString(2), // description
                        cursor.getString(3), // imgUrl
                        cursor.getString(4), // detailImgUrlFirst
                        cursor.getString(5), // detailImgUrlSecond
                        cursor.getString(6), // detailImgUrlThird
                        cursor.getDouble(7), // oldPrice
                        cursor.getDouble(8), // newPrice
                        cursor.getInt(9), // quantity
                        releaseDate, // releaseDate
                        cursor.getString(12), // status
                        cursor.getInt(10) // brandId
                );
                productList.add(product);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return productList;
    }


    ///AAA
    public List<Product> searchProductsByName(String productName) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Add quantity > 0 condition to the query
        String selection = PRODUCT_COLUMN_NAME + " LIKE ? AND " + PRODUCT_COLUMN_QUANTITY + " > ?";
        String[] selectionArgs = new String[]{"%" + productName + "%", "0"};

        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{
                        PRODUCT_COLUMN_ID, PRODUCT_COLUMN_NAME, PRODUCT_COLUMN_DESCRIPTION, PRODUCT_COLUMN_IMG_URL,
                        PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST, PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND, PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD,
                        PRODUCT_COLUMN_OLD_PRICE, PRODUCT_COLUMN_NEW_PRICE, PRODUCT_COLUMN_QUANTITY, PRODUCT_COLUMN_BRAND_ID,
                        PRODUCT_COLUMN_RELEASE_DATE, PRODUCT_COLUMN_STATUS},
                selection, selectionArgs, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            do {
                Date releaseDate = null;
                try {
                    releaseDate = dateFormat.parse(cursor.getString(11));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Product product = new Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6),
                        cursor.getDouble(7), cursor.getDouble(8), cursor.getInt(9), releaseDate, cursor.getString(12), cursor.getInt(10));
                productList.add(product);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return productList;
    }


    public void deleteAllProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, null, null);
        db.close();
    }

    public void insertBrand(Brand brand) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BRAND_COLUMN_NAME, brand.getName());
        db.insert(TABLE_BRAND, null, values);
        db.close();
    }

    // Hàm getActiveBrands
    public List<Brand> getActiveBrands() {
        List<Brand> brandList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BRAND, new String[]{
                        BRAND_COLUMN_ID, BRAND_COLUMN_NAME},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Brand brand = new Brand(cursor.getInt(0), cursor.getString(1));
                brandList.add(brand);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return brandList;
    }

    ///AAA
    public List<Product> getActiveProductsByBrandId(int brandId) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Add quantity > 0 condition to the query
        String selection = PRODUCT_COLUMN_STATUS + "=? AND " + PRODUCT_COLUMN_BRAND_ID + "=? AND " + PRODUCT_COLUMN_QUANTITY + " > ?";
        String[] selectionArgs = new String[]{"ACTIVE", String.valueOf(brandId), "0"};

        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{
                        PRODUCT_COLUMN_ID, PRODUCT_COLUMN_NAME, PRODUCT_COLUMN_DESCRIPTION, PRODUCT_COLUMN_IMG_URL,
                        PRODUCT_COLUMN_DETAIL_IMG_URL_FIRST, PRODUCT_COLUMN_DETAIL_IMG_URL_SECOND, PRODUCT_COLUMN_DETAIL_IMG_URL_THIRD,
                        PRODUCT_COLUMN_OLD_PRICE, PRODUCT_COLUMN_NEW_PRICE, PRODUCT_COLUMN_QUANTITY, PRODUCT_COLUMN_BRAND_ID,
                        PRODUCT_COLUMN_RELEASE_DATE, PRODUCT_COLUMN_STATUS},
                selection, selectionArgs, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            do {
                Date releaseDate = null;
                try {
                    releaseDate = dateFormat.parse(cursor.getString(11));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Product product = new Product(
                        cursor.getInt(0), // id
                        cursor.getString(1), // name
                        cursor.getString(2), // description
                        cursor.getString(3), // imgUrl
                        cursor.getString(4), // detailImgUrlFirst
                        cursor.getString(5), // detailImgUrlSecond
                        cursor.getString(6), // detailImgUrlThird
                        cursor.getDouble(7), // oldPrice
                        cursor.getDouble(8), // newPrice
                        cursor.getInt(9), // quantity
                        releaseDate, // releaseDate
                        cursor.getString(12), // status
                        cursor.getInt(10) // brandId
                );
                productList.add(product);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return productList;
    }


    public void deleteAllBrands() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BRAND, null, null);
        db.close();
    }

    public void deleteAllFavorite() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITE, null, null);
        db.close();
    }

    public List<Favorite> getFavoriteList(int userId) {
        List<Favorite> favoriteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVORITE, new String[]{"productId"},
                "userId=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(0);
                Product product = getProduct(productId); // Get product details from the Product table
                if (product != null) {
                    Favorite favorite = new Favorite(productId, userId, null, productId, product);
                    favoriteList.add(favorite);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return favoriteList;
    }

    public void insertFavorite(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userId", userId);
        values.put("productId", productId);

        db.insert(TABLE_FAVORITE, null, values);
        db.close();
    }

    public boolean removeFavorite(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.delete(TABLE_FAVORITE, "userId=? AND productId=?", new String[]{String.valueOf(userId), String.valueOf(productId)});

        return affectedRows > 0;
    }

//    public List<Order> getAllOrders(int userId) {
//        List<Order> orderList = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Update format as needed
//
//        Cursor cursor = db.query("`Order`", new String[]{"id", "totalAmount", "orderDate", "status"},
//                "userId=?", new String[]{String.valueOf(userId)}, null, null, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int orderId = cursor.getInt(0);
//                double totalAmount = cursor.getDouble(1);
//                String orderDateString = cursor.getString(2);
//                String status = cursor.getString(3);
//
//                Date orderDate = null;
//                try {
//                    orderDate = dateFormat.parse(orderDateString); // Parse the date
//                } catch (ParseException e) {
//                    e.printStackTrace(); // Handle parsing exception
//                }
//
//                Order order = new Order(orderId, userId, totalAmount, orderDate, status,null);
//                orderList.add(order);
//            } while (cursor.moveToNext());
//
//            cursor.close();
//        }
//
//        return orderList;
//    }
public List<Order> getAllOrders(int userId) {
    List<Order> orderList = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Updated SQL query to include product name
    String query = "SELECT o.id, o.totalAmount, o.orderDate, o.status, " +
            "od.address, od.phone, od.email, od.productId, od.quantity, od.price, od.imageUrl, od.productName " + // Join with product to get product name
            "FROM `Order` o " +
            "LEFT JOIN OrderDetail od ON o.id = od.orderId " +
            "LEFT JOIN Product p ON od.productId = p.id " +
            "WHERE o.userId = ?";

    Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

    int currentOrderId = -1;
    Order currentOrder = null;

    if (cursor != null && cursor.moveToFirst()) {
        do {
            // Order information
            int orderId = cursor.getInt(0);
            double totalAmount = cursor.getDouble(1);
            String orderDateString = cursor.getString(2);
            String status = cursor.getString(3);

            // OrderDetail information
            String address = cursor.getString(4);
            String phone = cursor.getString(5);
            String email = cursor.getString(6);
            int productId = cursor.getInt(7);
            int quantity = cursor.getInt(8);
            double price = cursor.getDouble(9);
            String imageUrl = cursor.getString(10); // Retrieve the image URL
            String productName = cursor.getString(11); // Retrieve the product name

            // Parse order date
            Date orderDate = null;
            try {
                orderDate = dateFormat.parse(orderDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // If the order ID changes, create a new Order object
            if (currentOrderId != orderId) {
                // If there's already a currentOrder, add it to the list
                if (currentOrder != null) {
                    orderList.add(currentOrder);
                }

                // Create a new list for OrderDetail entries
                List<OrderDetail> orderDetails = new ArrayList<>();

                // Create a new OrderDetail object for the current order
                OrderDetail orderDetail = new OrderDetail(0, orderId, userId, productId, quantity, price, address, phone, email, imageUrl, productName);
                orderDetails.add(orderDetail); // Add the first order detail to the list

                // Create a new Order object
                currentOrder = new Order(orderId, userId, totalAmount, orderDate, status, orderDetails);
                currentOrderId = orderId;  // Update the current order ID
            } else {
                // Add additional OrderDetail objects if the order ID is the same
                OrderDetail orderDetail = new OrderDetail(0, orderId, userId, productId, quantity, price, address, phone, email, imageUrl, productName);
                currentOrder.getOrderDetails().add(orderDetail); // Add to the existing order's details
            }

        } while (cursor.moveToNext());

        // Add the last processed order
        if (currentOrder != null) {
            orderList.add(currentOrder);
        }

        cursor.close();
    }

    return orderList;
}

    public void insertOrder(int userId, double totalAmount, String orderDate, String status, List<OrderDetail> orderDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Start transaction
        db.beginTransaction();
        try {
            // Insert order and get generated order ID
            ContentValues orderValues = new ContentValues();
            orderValues.put("userId", userId);
            orderValues.put("totalAmount", totalAmount);
            orderValues.put("orderDate", orderDate);
            orderValues.put("status", status);

            // Use double quotes to escape the table name
            long orderId = db.insert("\"Order\"", null, orderValues);

            // Check if order was inserted successfully
            if (orderId == -1) {
                throw new Exception("Failed to insert order");
            }

            // Insert each OrderDetail with the generated orderId
            for (OrderDetail detail : orderDetails) {
                ContentValues detailValues = new ContentValues();
                detailValues.put("orderId", orderId);  // Use generated orderId here
                detailValues.put("userId", detail.getUserId());
                detailValues.put("productId", detail.getProductId());
                detailValues.put("quantity", detail.getQuantity());
                detailValues.put("price", detail.getPrice());
                detailValues.put("address", detail.getAddress());
                detailValues.put("phone", detail.getPhone());
                detailValues.put("email", detail.getEmail());
                detailValues.put("imageUrl", detail.getImageUrl()); // Add image URL if needed
                detailValues.put("productName", detail.getProductName()); // Add product name if needed

                long detailId = db.insert("OrderDetail", null, detailValues);
                // Optionally check if detail was inserted successfully
                if (detailId == -1) {
                    throw new Exception("Failed to insert order detail for product ID: " + detail.getProductId());
                }
            }

            // Mark the transaction as successful
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace(); // Handle the error appropriately in your app
        } finally {
            // End the transaction
            db.endTransaction();
            db.close();
        }
    }

//    public void insertOrder(int userId, double totalAmount, String orderDate, String status, List<OrderDetail> orderDetails) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        // Start transaction
//        db.beginTransaction();
//        try {
//            int orderId = generateUniqueOrderId(db); // Generate unique order ID
//
//            // Insert order with generated order ID
//            ContentValues orderValues = new ContentValues();
//            orderValues.put("orderId", orderId); // Use generated unique order ID
//            orderValues.put("userId", userId);
//            orderValues.put("totalAmount", totalAmount);
//            orderValues.put("orderDate", orderDate);
//            orderValues.put("status", status);
//
//            long insertedOrderId = db.insert("\"Order\"", null, orderValues);
//
//            // Check if order was inserted successfully
//            if (insertedOrderId == -1) {
//                throw new Exception("Failed to insert order");
//            }
//
//            // Insert each OrderDetail with the generated orderId
//            for (OrderDetail detail : orderDetails) {
//                ContentValues detailValues = new ContentValues();
//                detailValues.put("orderId", orderId);  // Use generated unique orderId
//                detailValues.put("userId", detail.getUserId());
//                detailValues.put("productId", detail.getProductId());
//                detailValues.put("quantity", detail.getQuantity());
//                detailValues.put("price", detail.getPrice());
//                detailValues.put("address", detail.getAddress());
//                detailValues.put("phone", detail.getPhone());
//                detailValues.put("email", detail.getEmail());
//                detailValues.put("imageUrl", detail.getImageUrl());
//                detailValues.put("productName", detail.getProductName());
//
//                long detailId = db.insert("OrderDetail", null, detailValues);
//                // Optionally check if detail was inserted successfully
//                if (detailId == -1) {
//                    throw new Exception("Failed to insert order detail for product ID: " + detail.getProductId());
//                }
//            }
//
//            // Mark the transaction as successful
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            e.printStackTrace(); // Handle the error appropriately in your app
//        } finally {
//            // End the transaction
//            db.endTransaction();
//            db.close();
//        }
//    }
//
//    // Helper method to generate a unique order ID
//    private int generateUniqueOrderId(SQLiteDatabase db) {
//        int orderId;
//        Random random = new Random();
//        boolean isUnique;
//
//        do {
//            // Generate a random number (e.g., between 100000 and 999999)
//            orderId = 100000 + random.nextInt(900000);
//
//            // Check if this ID already exists in the Order table
//            String query = "SELECT COUNT(*) FROM \"Order\" WHERE orderId = ?";
//            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
//            cursor.moveToFirst();
//            int count = cursor.getInt(0);
//            cursor.close();
//
//            isUnique = (count == 0); // Unique if count is 0
//        } while (!isUnique);
//
//        return orderId;
//    }


    // Cart
    public void insertCartItem(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("productId", productId);
        values.put("quantity", quantity);
        db.insert("Cart", null, values);
        db.close();
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("Cart", new String[]{"id", "productId", "quantity"},
                "userId=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int productId = cursor.getInt(1);
                int quantity = cursor.getInt(2);
                cartItems.add(new CartItem(id, userId, null, productId, getProduct(productId), quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return cartItems;
    }

    public void updateCartItem(int cartItemId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        db.update("Cart", values, "id=?", new String[]{String.valueOf(cartItemId)});
        db.close();
    }

    public boolean deleteCartItem(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.delete("Cart", "userId=? AND productId=?", new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
        return affectedRows > 0;
    }


    public boolean updateUserAvatar(String userId, String avatarUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("avatarUrl", avatarUrl);

        // Update the avatar URL where the id matches (not googleId)
        int result = db.update("User", contentValues, "id = ?", new String[]{userId});
        db.close();
        return result > 0; // Returns true if at least one row was updated
    }



    public String getUserAvatarUrl(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String avatarUrl = null; // Initialize avatarUrl to null

        // Define the columns to fetch
        String[] columns = {"avatarUrl"};

        // Perform the query using the structured query() method
        Cursor cursor = db.query("User", columns, "id=?", new String[]{userId}, null, null, null);

        // Check if the cursor has any results
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String avatarUrlColumn = cursor.getString(cursor.getColumnIndexOrThrow("avatarUrl"));
            avatarUrl = avatarUrlColumn;
        }

        // Close the cursor to prevent memory leaks
        if (cursor != null) {
            cursor.close();
        }

        return avatarUrl; // Return the avatar URL or null if not found
    }





    @SuppressLint("Range")
    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("User", null, "id = ?", new String[]{userId}, null, null, null);
        User user = null; // Initialize user to null

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = new User();
                user.setId(Integer.parseInt(userId)); // Set the user ID
                user.setFullname(cursor.getString(cursor.getColumnIndex("fullname"))); // Load fullname
                user.setUsername(cursor.getString(cursor.getColumnIndex("username"))); // Load username
                user.setAddress(cursor.getString(cursor.getColumnIndex("address"))); // Load address
                user.setPhone(cursor.getString(cursor.getColumnIndex("phone"))); // Load phone
                user.setAvatarUrl(cursor.getString(cursor.getColumnIndex("avatarUrl"))); // Load avatar URL
            }
            cursor.close();
        }
        return user; // Return the user object or null if not found
    }

    public boolean updateUserProfile(String userId, String fullName, String username, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fullname", fullName);
        contentValues.put("username", username);
        contentValues.put("address", address);
        contentValues.put("phone", phone);
      //  contentValues.put("avatarUrl", avatarUrl); // Include avatar URL in updates

        // Update user profile where the user ID matches
        int result = db.update("User", contentValues, "id = ?", new String[]{userId});
        return result > 0; // Returns true if at least one row was updated
    }

    public int insertUserProfile(String googleId, String fullName, String username, String phone, String avatarUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fullname", fullName);
        contentValues.put("address", username);
        contentValues.put("phone", phone);
        contentValues.put("avatarUrl", avatarUrl); // Include avatar URL in insert

        // Check if the user already exists by googleId
        if (isGoogleUserExists(googleId)) {
            // Update the existing user record
            int rowsAffected = db.update("User", contentValues, "googleId = ?", new String[]{googleId});
            return rowsAffected > 0 ? getUserIdByGoogleId(googleId) : -1; // Return user ID if updated, otherwise -1
        } else {
            // Insert a new user record
            contentValues.put("googleId", googleId); // Add googleId to content values
            long result = db.insert("User", null, contentValues);
            return result != -1 ? (int) result : -1; // Return the inserted ID cast to int, or -1 if failed
        }
    }

    // Helper method to get user ID by Google ID
    private int getUserIdByGoogleId(String googleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("User", new String[]{"id"}, "googleId = ?", new String[]{googleId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return userId; // Return the found user ID
        }
        return -1; // Return -1 if user ID not found
    }

    // Helper method to check if the user exists based on googleId
    private boolean isGoogleUserExists(String googleId) {
        String query = "SELECT * FROM User WHERE googleId = ?";
        Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{googleId});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }





//    public void insertDemoData(SQLiteDatabase db) {
//        // Insert demo user
//        ContentValues userValues = new ContentValues();
//        userValues.put("fullname", "John Doe");
//        userValues.put("username", "johndoe");
//        userValues.put("password", "password123");
//        userValues.put("avatarUrl", "https://example.com/avatar.png");
//        userValues.put("address", "123 Main St, Anytown, USA");
//        userValues.put("phone", "1234567890");
//        long userId = db.insert("User", null, userValues);
//
//        // Insert demo brand
//        ContentValues brandValues = new ContentValues();
//        brandValues.put("name", "Sony");
//        long brandId = db.insert("Brand", null, brandValues);
//
//        // Insert demo products
//        ContentValues product1 = new ContentValues();
//        product1.put("name", "PlayStation 5 Controller");
//        product1.put("description", "The latest controller for the PS5");
//        product1.put("imgUrl", "https://example.com/product1.jpg");
//        product1.put("detailImgUrlFirst", "https://example.com/product1_detail1.jpg");
//        product1.put("detailImgUrlSecond", "https://example.com/product1_detail2.jpg");
//        product1.put("detailImgUrlThird", "https://example.com/product1_detail3.jpg");
//        product1.put("oldPrice", 70.00);
//        product1.put("newPrice", 60.00);
//        product1.put("quantity", 10);
//        product1.put("brandId", brandId);
//        product1.put("releaseDate", "2023-10-25");
//        product1.put("status", "available");
//        long productId1 = db.insert("Product", null, product1);
//
//        ContentValues product2 = new ContentValues();
//        product2.put("name", "Xbox Series X Controller");
//        product2.put("description", "The latest controller for the Xbox Series X");
//        product2.put("imgUrl", "https://example.com/product2.jpg");
//        product2.put("detailImgUrlFirst", "https://example.com/product2_detail1.jpg");
//        product2.put("detailImgUrlSecond", "https://example.com/product2_detail2.jpg");
//        product2.put("detailImgUrlThird", "https://example.com/product2_detail3.jpg");
//        product2.put("oldPrice", 65.00);
//        product2.put("newPrice", 55.00);
//        product2.put("quantity", 15);
//        product2.put("brandId", brandId);
//        product2.put("releaseDate", "2023-10-25");
//        product2.put("status", "available");
//        long productId2 = db.insert("Product", null, product2);
//
//        // Insert demo order
//        ContentValues orderValues = new ContentValues();
//        orderValues.put("userId", userId);
//        orderValues.put("totalAmount", 115.00);
//        orderValues.put("orderDate", "2024-10-25");
//        orderValues.put("status", "success");
//        long orderId = db.insert("`Order`", null, orderValues);
//
//        // Insert demo order details
//        ContentValues orderDetail1 = new ContentValues();
//        orderDetail1.put("orderId", orderId);
//        orderDetail1.put("userId", userId);
//        orderDetail1.put("productId", productId1);
//        orderDetail1.put("quantity", 1);
//        orderDetail1.put("price", 60.00);
//        orderDetail1.put("address", "123 Main St, Anytown, USA");
//        orderDetail1.put("phone", "1234567890");
//        orderDetail1.put("email", "johndoe@example.com");
//        db.insert("OrderDetail", null, orderDetail1);
//
//        ContentValues orderDetail2 = new ContentValues();
//        orderDetail2.put("orderId", orderId);
//        orderDetail2.put("userId", userId);
//        orderDetail2.put("productId", productId2);
//        orderDetail2.put("quantity", 1);
//        orderDetail2.put("price", 55.00);
//        orderDetail2.put("address", "123 Main St, Anytown, USA");
//        orderDetail2.put("phone", "1234567890");
//        orderDetail2.put("email", "johndoe@example.com");
//        db.insert("OrderDetail", null, orderDetail2);
//    }

    @SuppressLint("Range")
    public String getUserFullName(int userId) {
        String fullName = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT fullname FROM User WHERE id = ?", new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                // Make sure to use the exact column name as it is in your database schema
                fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            }
        } finally {
            // Always close the cursor to avoid memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return fullName;
    }

    @SuppressLint("Range")
    public String getUserAddress(int userId) {
        String address = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT address FROM User WHERE id = ?", new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return address;
    }

    @SuppressLint("Range")
    public String getUserPhone(int userId) {
        String phone = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT phone FROM User WHERE id = ?", new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return phone;
    }
    public void updateProductQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity); // Adjust to match your product quantity column name
        db.update("Product", values, "id = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public int getProductQuantity(int productId) {
        int quantity = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{PRODUCT_COLUMN_QUANTITY},
                PRODUCT_COLUMN_ID + "=?", new String[]{String.valueOf(productId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            quantity = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return quantity;
    }

    public boolean isProductInCart(int userId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM cart WHERE userId = ? AND productId = ?", new String[]{String.valueOf(userId), String.valueOf(productId)});

        if (cursor != null) {
            cursor.moveToFirst();
            boolean exists = cursor.getInt(0) > 0; // If count > 0, product exists
            cursor.close();
            return exists;
        }

        return false; // Default return if cursor is null
    }



}


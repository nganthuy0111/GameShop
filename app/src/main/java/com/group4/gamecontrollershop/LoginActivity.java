package com.group4.gamecontrollershop;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    CallbackManager callbackManager; // For Facebook Login
    SQLiteDatabase myDB;

    private EditText username, password;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize views
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        // Initialize SQLite Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        myDB = dbHelper.getWritableDatabase();

        // Configure Google Sign-In
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, options);

        // Initialize Facebook Login
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Facebook Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Facebook Login Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Google Sign-In button
        findViewById(R.id.google).setOnClickListener(view -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        // Facebook Sign-In button
        findViewById(R.id.facebook).setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        });

        loginButton.setOnClickListener(view -> loginUser());
    }

    // ActivityResultLauncher for Google Sign-In
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            handleGoogleSignInResult(account);
                        }
                    } catch (ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Handle Google Sign-In result
    private void handleGoogleSignInResult(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the user's Google ID and other details
                String googleId = account.getId(); // Google unique ID
                String email = account.getEmail();
                String avatarUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

                // Check if the user exists in the local database by Google ID
                Cursor cursor = myDB.rawQuery("SELECT id FROM User WHERE googleId = ?", new String[]{googleId});
                if (cursor != null && cursor.moveToFirst()) {
                    // Get the user ID from the cursor
                    @SuppressLint("Range") String idFromCursor = cursor.getString(cursor.getColumnIndex("id"));

                    // Save user ID in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", idFromCursor); // Save the ID from the database
                    editor.apply(); // Save changes asynchronously

                    cursor.close(); // Close the cursor

                    // User already exists, navigate to MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish(); // End this activity
                } else {
                    // User does not exist, insert into local database
                    insertGoogleUser(googleId, email, avatarUrl);

                    // Redirect to a profile setup activity
                    Intent setupIntent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
                    startActivity(setupIntent);
                    finish();
                }
            } else {
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle Facebook login
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the user's Facebook ID and other details
                String facebookId = token.getUserId();
                String email = token.getUserId(); // Use Facebook email if available
                String avatarUrl = ""; // Optionally fetch the profile picture URL if needed

                // Check if the user exists in the local database by Facebook ID
                Cursor cursor = myDB.rawQuery("SELECT id FROM User WHERE facebookId = ?", new String[]{facebookId});
                if (cursor != null && cursor.moveToFirst()) {
                    // Get the user ID from the cursor
                    @SuppressLint("Range") String idFromCursor = cursor.getString(cursor.getColumnIndex("id"));

                    // Save user ID in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", idFromCursor); // Save the ID from the database
                    editor.apply(); // Save changes asynchronously

                    cursor.close(); // Close the cursor

                    // User already exists, navigate to MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish(); // End this activity
                } else {
                    // User does not exist, insert into local database
                    insertFacebookUser(facebookId, email, avatarUrl);

                    // Redirect to a profile setup activity
                    Intent setupIntent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
                    startActivity(setupIntent);
                    finish();
                }
            } else {
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // This method is triggered when the user clicks on the Sign Up link
    public void onSignUpClick(View view) {
        // Navigate to the RegisterActivity
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    // Check if Google user already exists in the local database
    private boolean isGoogleUserExists(String googleId) {
        String query = "SELECT * FROM User WHERE googleId = ?";
        Cursor cursor = myDB.rawQuery(query, new String[]{googleId});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void loginUser() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Validate fields
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check user credentials
        Cursor cursor = myDB.rawQuery("SELECT * FROM User WHERE username = ? AND password = ?", new String[]{user, pass});

        if (cursor.moveToFirst()) {
            // Login successful
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            // Get the user ID from the cursor
            @SuppressLint("Range") String userId = cursor.getString(cursor.getColumnIndex("id"));

            // Save user ID in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userId", userId);
            editor.apply(); // Save changes asynchronously

            // Navigate to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity
        } else {
            // Login failed
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void insertGoogleUser(String googleId, String email, String avatarUrl) {
        ContentValues values = new ContentValues();
        values.put("googleId", googleId);
        values.put("username", email); // You can use email or set a default username
        values.put("avatarUrl", avatarUrl);
        values.put("fullname", ""); // You can set default or leave it empty
        values.put("address", ""); // Optional: leave empty or set default
        values.put("status", ""); // Optional: leave empty or set default
        values.put("phone", ""); // Optional: leave empty or set default
        myDB.insert("User", null, values);
    }

    private void insertFacebookUser(String facebookId, String email, String avatarUrl) {
        ContentValues values = new ContentValues();
        values.put("facebookId", facebookId);
        values.put("username", email); // You can use email or set a default username
        values.put("avatarUrl", avatarUrl);
        values.put("fullname", ""); // You can set default or leave it empty
        values.put("address", ""); // Optional: leave empty or set default
        values.put("status", ""); // Optional: leave empty or set default
        values.put("phone", ""); // Optional: leave empty or set default
        myDB.insert("User", null, values);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data); // Pass the activity result back to the Facebook SDK
    }
}

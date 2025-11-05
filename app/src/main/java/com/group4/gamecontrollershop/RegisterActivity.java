package com.group4.gamecontrollershop;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.group4.gamecontrollershop.database_helper.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

        private EditText fullname, username, email, password, confirmPassword;
        private Button registerButton;
        private DatabaseHelper myDb;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.register);

            // Initialize views
            fullname = findViewById(R.id.fullname);
            username = findViewById(R.id.username);
            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            confirmPassword = findViewById(R.id.confirm_password);
            registerButton = findViewById(R.id.registerButton);

            // Initialize the database helper
            myDb = new DatabaseHelper(this);

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();
                }
            });
        }

        private void registerUser() {
            String name = fullname.getText().toString().trim();
            String user = username.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            // Validate fields
            if (name.isEmpty() || user.isEmpty() || emailStr.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add user to the database
            SQLiteDatabase db = myDb.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", user);
            values.put("password", pass); // Consider hashing the password before saving
            values.put("avatarUrl", ""); // Add default or user-selected avatar URL
            values.put("address", ""); // You can get this from the user as needed
            values.put("googleId", (Integer) null); // Google ID, if applicable
            values.put("status", "notverified"); // Default status
            values.put("phone", emailStr); // Phone number if applicable

            long newRowId = db.insert("User", null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                // Navigate back to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close RegisterActivity
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }

        // This method is triggered when the user clicks on the Login link
        public void onLoginClick(View view) {
            // Navigate back to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close RegisterActivity
        }
    }
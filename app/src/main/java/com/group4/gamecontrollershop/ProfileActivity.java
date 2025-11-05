package com.group4.gamecontrollershop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.databinding.ActivityProfileBinding;
import android.Manifest; // This is necessary for using the Manifest.permission constants

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 300;
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int CAPTURE_IMAGE_REQUEST = 200;

    private ActivityProfileBinding binding;
    private Uri imageUri;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private DatabaseHelper databaseHelper;

    private FirebaseAuth firebaseAuth;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("avatars");
        databaseHelper = new DatabaseHelper(this); // Initialize your database helper

        // Load the avatar image from the database
        loadAvatarImage();

        // Upload Image Button
        binding.uploadImageBtn.setOnClickListener(v -> selectImageSource());

        // Set up the sign-out button
        binding.StFifthLayout.setOnClickListener(v -> signOut());


        // Set up click listeners for navigation
        binding.BoostButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, AboutActivity.class)));
        binding.EditButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        binding.SettingsButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingActivity.class)));

        // Get userId from SharedPreferences instead of Firebase
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Optionally, load more user data using userId
            //Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No user ID found, please log in.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if userId is not found
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadAvatarImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            String avatarUrl = databaseHelper.getUserAvatarUrl(userId);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(avatarUrl)
                        .into(binding.profileImage);
            } else {
                // Load default avatar or handle the absence of an avatar
                binding.profileImage.setImageResource(R.drawable.ic_profile);
            }
        }
    }

    private void selectImageSource() {
        String[] options = {"Select from Gallery", "Capture from Camera"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        selectImageFromGallery();
                    } else {
                        captureImageFromCamera();
                    }
                })
                .show();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImageFromCamera() {
        if (checkCameraPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
        } else {
            requestCameraPermission();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                binding.profileImage.setImageURI(imageUri);
                uploadImage(); // Automatically upload after selecting
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                imageUri = (Uri) data.getExtras().get("data"); // Correctly retrieve image URI from camera
                binding.profileImage.setImageURI(imageUri);
                uploadImage(); // Automatically upload after capturing
            }
        }
    }

    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        String fileName = formatter.format(new Date());
        StorageReference userProfileImageRef = storageReference.child(userId + "/" + fileName); // Save under avatars/{userId}/filename

        // Lambda expressions will reference userId here, so we need it to be effectively final
        userProfileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> userProfileImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    // Save the URL in the database
                    saveAvatarUrlToDatabase(userId, downloadUri.toString());
                    Toast.makeText(ProfileActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                })).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveAvatarUrlToDatabase(String userId, String avatarUrl) {
        boolean isUpdated = databaseHelper.updateUserAvatar(userId, avatarUrl);
        if (isUpdated) {
            Toast.makeText(this, "Avatar URL saved in database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save avatar URL in database", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now you can capture the image
                captureImageFromCamera();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Sign out method for Firebase and Google account
    private void signOut() {
        // Clear the SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Firebase sign out
        firebaseAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(ProfileActivity.this, "Google Account Disconnected", Toast.LENGTH_SHORT).show();
        });

        // Remove the saved Google account from device
        googleSignInClient.revokeAccess().addOnCompleteListener(this, task -> {
            Toast.makeText(ProfileActivity.this, "Google Account Removed from Device", Toast.LENGTH_SHORT).show();
        });

        // Redirect to LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
    }
}



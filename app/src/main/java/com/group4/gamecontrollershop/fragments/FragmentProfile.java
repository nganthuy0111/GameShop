package com.group4.gamecontrollershop.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.gamecontrollershop.AboutActivity;
import com.group4.gamecontrollershop.EditProfileActivity;
import com.group4.gamecontrollershop.LoginActivity;
import com.group4.gamecontrollershop.ProfileActivity;
import com.group4.gamecontrollershop.R;
import com.group4.gamecontrollershop.SettingActivity;
import com.group4.gamecontrollershop.database_helper.DatabaseHelper;
import com.group4.gamecontrollershop.databinding.FragmentProfileBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentProfile extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 300;
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int CAPTURE_IMAGE_REQUEST = 200;

    private FragmentProfileBinding binding;
    private Uri imageUri;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private DatabaseHelper databaseHelper;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        Context context = requireContext();

        firebaseAuth = FirebaseAuth.getInstance();

        // Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);

        storageReference = FirebaseStorage.getInstance().getReference("avatars");
        databaseHelper = new DatabaseHelper(context);

        loadAvatarImage();

        binding.uploadImageBtn.setOnClickListener(v -> selectImageSource());

        // Set up the sign-out button
        binding.StFifthLayout.setOnClickListener(v -> signOut());

        // Set up listeners for navigation
        binding.BoostButton.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutActivity.class)));
        binding.EditButton.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
        binding.SettingsButton.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingActivity.class)));

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            Toast.makeText(context, "No user ID found, please log in.", Toast.LENGTH_SHORT).show();
            navigateToLoginActivity();
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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


    private void loadAvatarImage() {
        Context context = requireContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            String avatarUrl = databaseHelper.getUserAvatarUrl(userId);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(avatarUrl)
                        .into(binding.profileImage);
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_profile);
            }
        }
    }

    private void selectImageSource() {
        String[] options = {"Select from Gallery", "Capture from Camera"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        selectImageFromGallery();
                    } else {
                        try {
                            captureImageFromCamera();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .show();
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImageFromCamera() throws IOException {
        if (checkCameraPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageUri = FileProvider.getUriForFile(requireContext(), "com.group4.gamecontrollershop.fileprovider", createImageFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
        } else {
            requestCameraPermission();
        }
    }





    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        String fileName = formatter.format(new Date());
        StorageReference userProfileImageRef = storageReference.child(userId + "/" + fileName);

        userProfileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> userProfileImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    saveAvatarUrlToDatabase(userId, downloadUri.toString());
                    Toast.makeText(getContext(), "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                })).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to Upload", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveAvatarUrlToDatabase(String userId, String avatarUrl) {
        boolean isUpdated = databaseHelper.updateUserAvatar(userId, avatarUrl);
        if (isUpdated) {
            Toast.makeText(getContext(), "Avatar URL saved in database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to save avatar URL in database", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void signOut() {
        // Clear the SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Firebase sign out
        firebaseAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            Toast.makeText(getContext(), "Google Account Disconnected", Toast.LENGTH_SHORT).show();

            // Remove the saved Google account from device
            googleSignInClient.revokeAccess().addOnCompleteListener(requireActivity(), task2 -> {
                Toast.makeText(getContext(), "Google Account Removed from Device", Toast.LENGTH_SHORT).show();

                // Redirect to LoginActivity after sign out completes
                navigateToLoginActivity();
            });
        });
    }





    private void navigateToLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}

package com.example.sub_admin_app;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView copyright;
    private ImageView back,profile;
    private Button logout;
    private TextView name, id, phone;
    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    private SharedPreferences sp;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.tvusername);
        id = findViewById(R.id.tvuserId);
        phone = findViewById(R.id.tvusernumber);

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);
        logout = findViewById(R.id.tvPasswordlabel);

        profile = findViewById(R.id.iv_editProfile);
        copyright = findViewById(R.id.tvcopyrightprofile);
        back = findViewById(R.id.iv_ProfileBack);

        name.setText(sp.getString("name","SubAdmin"));
        id.setText(sp.getString("id","01"));
        phone.setText(sp.getString("phone","0000000000"));

        profile.setOnClickListener(v -> requestStoragePermission());

        copyright.setOnClickListener(v -> {
            OpenwhatsappOP.openWhatsapp(this);
        });

        logout.setOnClickListener(v -> {
            sp.edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, loginActivity.class));
            finish();
        });

        back.setOnClickListener(v -> finish());
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_PERMISSION);
            } else {
                openGallery();
            }
        } else {
            // Android 12 or below
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            } else {
                openGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadImageToFirebase(imageUri);
            }
        }
    }




//    private File getFileFromUri(Uri uri) {
//        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
//            if (inputStream == null) return null;
//            File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
//            try (FileOutputStream out = new FileOutputStream(tempFile)) {
//                byte[] buffer = new byte[8192];
//                int read;
//                while ((read = inputStream.read(buffer)) != -1) {
//                    out.write(buffer, 0, read);
//                }
//                out.flush();
//            }
//            return tempFile;
//        } catch (Exception e) {
//            Log.e(TAG, "getFileFromUri error", e);
//            return null;
//        }
//    }

  private void uploadImageToFirebase(Uri imageUri) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profilePics/" + sp.getString("id","01") + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Save link to Realtime Database
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("SubAdmins").child(sp.getString("id","01"));
                            dbRef.child("profilePic").setValue(uri.toString())
                                    .addOnSuccessListener(unused -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save link", Toast.LENGTH_SHORT).show());
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show());
    }


    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("SubAdmins")
                .child(sp.getString("id","01")) // Replace with the logged-in subadmin's ID
                .child("profilePic");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageUrl = snapshot.getValue(String.class);

                    // Load image into ImageView using Glide
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.edit_profile)
                                .into(profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
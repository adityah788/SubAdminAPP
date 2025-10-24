package com.example.sub_admin_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageView profile, notification, menu;
    private LinearLayout layoutDots;
    private BannerAdapter bannerAdapter;
    private List<String> bannerImages = new ArrayList<>();
    private ImageView[] dots;
    private Handler handler = new Handler();
    private int currentPage = 0;
    private int bannerCount;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private SharedPreferences sp;
    private TextView tvAvailableValue, tvUsedValue,tv_badge;
    private DatabaseReference bannerRef;
    private String currentUserId;
    private ValueEventListener chatListener;
    private AlertDialog blockDialog;
    private DatabaseReference statusRef;
    private ValueEventListener statusListener;

    String adminName,adminPhone,adminProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile = findViewById(R.id.ivProfile);
        layoutDots = findViewById(R.id.layoutDots);
        notification = findViewById(R.id.iv_notification);
        menu = findViewById(R.id.ivMenu);

        tvAvailableValue = findViewById(R.id.tvAvailableValue);
        tvUsedValue = findViewById(R.id.tvUsedValue);
        tv_badge = findViewById(R.id.tv_badge1);

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);

        currentUserId = sp.getString("id","01");

        recyclerView = findViewById(R.id.rcvMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize with default banner images
//        bannerImages.add("unappad");
//        bannerImages.add("engineeringtad");
//        bannerImages.add("entheosad");

        mainAdapter = new MainAdapter(this, bannerImages, 0, 0);
        recyclerView.setAdapter(mainAdapter);

        bannerCount = bannerImages.size();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d("FCM Token", token);
                // Check this is the token your backend uses
            }
        });



        profile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
        });

        mainAdapter.setOnCopyrightClickListener(() -> {
            OpenwhatsappOP.openWhatsapp(this);
        });

        mainAdapter.setOnVideoClickListener(() -> {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            startActivity(intent);
        });

        mainAdapter.setOnUsersClickListener(() -> {
            Intent intent = new Intent(MainActivity.this, AllCustomerActivity.class);
            startActivity(intent);
        });

        mainAdapter.setAndroidClickListener(() -> {
            Intent intent = new Intent(MainActivity.this,AddAndroidDeviceActivity.class);
            startActivity(intent);
        });

        mainAdapter.setIosClickListener(() -> {
            Intent intent = new Intent(MainActivity.this, AddIOSDeviceActivity.class);
            startActivity(intent);
        });

        notification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        menu.setOnClickListener(v -> {
            showBottomMenu();
        });

        mainAdapter.setOnSupportClickListener(this::showSendUpdateChoiceDialog);

        // Load banners from Firebase
        loadBannersFromFirebase();

        // Test Firebase connection
        testFirebaseConnection();

        // Add test button for banner testing
        addBannerTestButton();

        setupBadgeListener();

        listenForUnreadNotifications();

        DatabaseReference adminRef = FirebaseDatabase.getInstance()
                .getReference("Admin");

// Fetch admin details once
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    adminName = snapshot.child("name").getValue(String.class);
                    adminPhone = snapshot.child("phone").getValue(String.class);
                    adminProfile = snapshot.child("profilePic").getValue(String.class);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load admin details", Toast.LENGTH_SHORT).show();
            }
        });

        checkBlockStatus();
    }

    private void addBannerTestButton() {
        // Add a test button to manually test banner loading
        android.widget.Button testButton = new android.widget.Button(this);
        testButton.setText("Test Banners");
        testButton.setOnClickListener(v -> {
            Log.d("BannerTest", "Test button clicked");
            loadBannersFromFirebase();
        });

        // For now, just log the click
        Log.d("BannerTest", "Banner test button added");
    }

    private void testFirebaseConnection() {
        DatabaseReference testRef = FirebaseDatabase.getInstance().getReference("AdminSettings");
        testRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseTest", "AdminSettings data: " + snapshot.exists());
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Log.d("FirebaseTest", "Child: " + child.getKey() + " = " + child.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseTest", "Error: " + error.getMessage());
            }
        });
    }

    private void loadBannersFromFirebase() {
        bannerRef = FirebaseDatabase.getInstance().getReference("Banners");
        bannerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> bannerUrls = new ArrayList<>();
                    for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                        String bannerUrl = bannerSnapshot.child("imageUrl").getValue(String.class);
                        if (bannerUrl != null && !bannerUrl.isEmpty()) {
                            bannerUrls.add(bannerUrl);
                        }
                    }
                    if (!bannerUrls.isEmpty()) {
                        // Update adapter with new banner URLs
                        mainAdapter.updateBanners(bannerUrls);
                        Log.d("Banners", "Loaded " + bannerUrls.size() + " banners from Firebase");

                        // Show banner info in toast for testing
                        // String bannerInfo = "Loaded " + bannerUrls.size() + " banners";
                        // Toast.makeText(MainActivity.this, bannerInfo, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Banners", "No banner URLs found in Firebase, using default");
                        Toast.makeText(MainActivity.this, "No banners found, using default", Toast.LENGTH_SHORT).show();
                        mainAdapter.resetToDefaultBanners();
                    }
                } else {
                    Log.d("Banners", "Banners node doesn't exist in Firebase, using default");
                    Toast.makeText(MainActivity.this, "Banners node not found, using default", Toast.LENGTH_SHORT).show();
                    mainAdapter.resetToDefaultBanners();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error loading banners: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error loading banners", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void showBottomMenu() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        // Set onClickListeners for each item:
        view.findViewById(R.id.M_users).setOnClickListener(v -> {
            // Open Users
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, AllCustomerActivity.class);
            startActivity(intent);
        });
        view.findViewById(R.id.M_keys_report).setOnClickListener(v -> {
            dialog.dismiss();

            // remaining to make
        });
        view.findViewById(R.id.menu_installation_video).setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this,VideoActivity.class));
        });

        view.findViewById(R.id.menu_manage_payment_qr).setOnClickListener(v -> {
            dialog.dismiss();

            // remaining to make
        });
        view.findViewById(R.id.menu_edit_account_details).setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
        view.findViewById(R.id.menu_logout).setOnClickListener(v -> {
            dialog.dismiss();
            sp.edit().clear().apply();
            startActivity(new Intent(MainActivity.this, loginActivity.class));
            finish();
        });

        dialog.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainAdapter != null) mainAdapter.stopAutoScroll();

        if (statusRef != null && statusListener != null) {
            statusRef.removeEventListener(statusListener);
        }
        if (blockDialog != null && blockDialog.isShowing()) {
            blockDialog.dismiss();
        }
    }

    private void showSendUpdateChoiceDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_choose_support, null);

        Button btnCallSupport = dialogView.findViewById(R.id.btn_call_support);
        Button btnChat = dialogView.findViewById(R.id.btn_chat_admin);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        btnCallSupport.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+"+adminPhone));
            startActivity(intent);
        });

        btnChat.setOnClickListener(v -> {
            dialog.dismiss();
            // Move to Support Activity
            Intent intent =  new Intent(MainActivity.this, SupportActivity.class);
            intent.putExtra("adminName",adminName);
            intent.putExtra("adminPhone",adminPhone);
            intent.putExtra("adminProfile",adminProfile);
            startActivity(intent);
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkBlockStatus();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("SubAdmins")
                .child(sp.getString("id","01")); // your subadmin id

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long keys= snapshot.child("keys").getValue(Long.class);

                    // Make sure to check for null
                    int totalKeys = keys != null ? keys.intValue() : 0;

                    // Get used keys count from Firebase
                    loadUsedKeysCount(totalKeys);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void loadUsedKeysCount(int totalKeys) {
        DatabaseReference usedKeysRef = FirebaseDatabase.getInstance()
                .getReference("UsedKeys")
                .child(sp.getString("id", "01"));

        usedKeysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int usedKeysCount = (int) snapshot.getChildrenCount();
//                int availableKeys = totalKeys - usedKeysCount;

//                tvAvailableValue.setText(String.valueOf(availableKeys));
//                tvUsedValue.setText(String.valueOf(usedKeysCount));

                // Update adapter with new values
                if (mainAdapter != null) {
                    mainAdapter.updateKeys(totalKeys, usedKeysCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error loading used keys: " + error.getMessage());
            }
        });
    }

    private void setupBadgeListener(){
        bannerRef = FirebaseDatabase.getInstance().getReference("Chats").child(currentUserId);

        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unreadCount = 0;
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        ChatMessage msg = messageSnapshot.getValue(ChatMessage.class);
                        if (msg != null && !msg.isSeen()) {
                            unreadCount++;
                        }
                    }
                updateBadge(unreadCount);
                mainAdapter.setUnreadCount(unreadCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };


        // Attach the listener (maybe in onResume or onCreate)
        bannerRef.addValueEventListener(chatListener);
    }

    private void updateBadge(int count) {
        if (tv_badge != null) {
            if (count > 0) {
                tv_badge.setVisibility(View.VISIBLE);
                tv_badge.setText(String.valueOf(count));
            } else {
                tv_badge.setVisibility(View.GONE);
            }
        }
    }

    private void listenForUnreadNotifications() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AlertMessages");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unreadCount = 0;

                for (DataSnapshot notifSnap : snapshot.getChildren()) {
                    DataSnapshot recipientSnap = notifSnap.child("recipients").child(currentUserId);

                    if (recipientSnap.exists()) {
                        Boolean seen = recipientSnap.child("seen").getValue(Boolean.class);
                        if (seen != null && !seen) {
                            unreadCount++;
                        }
                    }
                }

                // 🔴 Update badge
                if (unreadCount > 0) {
                    tv_badge.setText(String.valueOf(unreadCount));
                    tv_badge.setVisibility(View.VISIBLE);
                } else {
                    tv_badge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void checkBlockStatus() {

        statusRef = FirebaseDatabase.getInstance().getReference("SubAdmins")
                .child(currentUserId)
                .child("status");

        statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if ("Blocked".equalsIgnoreCase(status)) {
                        showBlockDialog();
                    } else {
                        dismissBlockDialog();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BlockCheck", "Error checking block status: " + error.getMessage());
            }
        };

        statusRef.addValueEventListener(statusListener);
    }

    private void showBlockDialog() {
        if (blockDialog != null && blockDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.BlockDialogTheme);
        builder.setTitle("Account Blocked");
        builder.setMessage("Your account has been blocked by the Main Admin. Please contact the Main Admin for assistance.");
        builder.setCancelable(false); // Prevent dismissal by back button
        builder.setPositiveButton("Contact Admin", (dialog, which) -> {
            contactMainAdmin();
        });

        // Create dialog with custom theme
        blockDialog = builder.create();
        blockDialog.setCanceledOnTouchOutside(false); // Prevent dismissal by touching outside

        // Override back button press
        blockDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                // Prevent back button from closing dialog
                return true;
            }
            return false;
        });

        // Show dialog
        if (!isFinishing() && !isDestroyed()) {
            blockDialog.show();

            // Customize button colors
            Button positiveButton = blockDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.red));
            }
        }
    }

    private void dismissBlockDialog() {
        if (blockDialog != null && blockDialog.isShowing()) {
            blockDialog.dismiss();
        }
    }

    private void contactMainAdmin() {
        // Open dialer with admin number
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+"+adminPhone));
        startActivity(intent);
    }



}
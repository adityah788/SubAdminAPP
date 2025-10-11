package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.util.Log;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notifications> list;
    private ImageView back;
    private SharedPreferences sp;
    private String subAdminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);
        subAdminId = sp.getString("id", "01");

        back = findViewById(R.id.iv_ProfileBack);
        recyclerView = findViewById(R.id.rv_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new NotificationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadAlerts();

        back.setOnClickListener(v -> finish());
        
        // Add test button for debugging
        addTestButton();

        markNotificationsAsSeen();
    }

    private void addTestButton() {
        // Add a test button to manually test notification loading
        android.widget.Button testButton = new android.widget.Button(this);
        testButton.setText("Test Notifications");
        testButton.setOnClickListener(v -> {
            Log.d("Notifications", "Test button clicked");
            loadAlerts();
        });
        
        // For now, just log the click
        Log.d("Notifications", "Test button added");
    }

    private void loadAlerts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AlertMessages");
        Log.d("Notifications", "Loading alerts from Firebase...");

        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                String subAdminId = sp.getString("id", "01");
                Log.d("Notifications", "SubAdmin ID: " + subAdminId);
                Log.d("Notifications", "Alerts exist: " + snapshot.exists());
                Log.d("Notifications", "Alerts count: " + snapshot.getChildrenCount());

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        // Instead of full mapping, get fields individually and handle recipients carefully
                        String message = ds.child("message").getValue(String.class);
                        Long timestamp = ds.child("timestamp").getValue(Long.class);
                        String adminId = ds.child("adminId").getValue(String.class);
                        String adminName = ds.child("adminName").getValue(String.class);
                        String adminProfilePic = ds.child("adminProfilePic").getValue(String.class);
                        String notificationId = ds.child("notificationId").getValue(String.class);

                        // Get recipients safely
                        Object recipientsObject = ds.child("recipients").getValue();
                        List<String> recipientsList = new ArrayList<>();

                        if (recipientsObject instanceof List) {
                            recipientsList = (List<String>) recipientsObject;
                        } else if (recipientsObject instanceof Map) {
                            // If stored as Map (HashMap), extract keys as recipient IDs
                            recipientsList = new ArrayList<>(((Map)recipientsObject).keySet());
                        }

                        // Only add if recipients contains this subadmin id or some other condition
                        if (recipientsList.contains(subAdminId) || recipientsList.contains("all") || recipientsList.isEmpty()) {
                            Notifications n = new Notifications();
                            n.setMessage(message);
                            if (timestamp != null) n.setTimestamp(timestamp);
                            n.setAdminId(adminId);
                            n.setAdminName(adminName);
                            n.setAdminProfilePic(adminProfilePic);
                            n.setNotificationId(notificationId);
                            n.setRecipients(recipientsList);

                            list.add(n);
                            Log.d("Notifications", "Added notification to list");
                        } else {
                            Log.d("Notifications", "Notification not for this subadmin");
                        }
                    } catch (Exception e) {
                        Log.e("Notifications", "Error parsing notification, ignoring this item", e);
                        // Skip this item but continue processing others
                    }
                }

                Log.d("Notifications", "Final list size: " + list.size());
                adapter.notifyDataSetChanged();

                if (!list.isEmpty()) {
                    // Optional toast
                } else {
                    Toast.makeText(NotificationActivity.this, "No notifications found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Notifications", "Error loading alerts: " + error.getMessage());
                Toast.makeText(NotificationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markNotificationsAsSeen() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AlertMessages");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot notifSnap : snapshot.getChildren()) {
                    if (notifSnap.child("recipients").hasChild(subAdminId)) {
                        notifSnap.getRef().child("recipients").child(subAdminId).child("seen").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}
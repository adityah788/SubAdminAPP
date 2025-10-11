package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupportActivity extends AppCompatActivity {

    private RecyclerView chatRecycler;
    ImageView back,ivuser;
    private List<ChatMessage> chatList;
    private ChatAdapter adapter;
    private DatabaseReference chatRef;
    private SharedPreferences sp;
    private String subAdminId;
    private TextView tv_adminName;
    String profilePic;
    String name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
//        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);
        subAdminId = sp.getString("id", "01");

        ivuser = findViewById(R.id.ivuser);
        back = findViewById(R.id.iv_supportBack);
        chatRecycler = findViewById(R.id.rv_adminChat);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));

        tv_adminName = findViewById(R.id.tv_adminName);

        DatabaseReference adminRef = FirebaseDatabase.getInstance()
                .getReference("Admin");

// Fetch admin details once
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name = snapshot.child("name").getValue(String.class);
                    profilePic = snapshot.child("profilePic").getValue(String.class);

                    tv_adminName.setText(name);

                    Glide.with(SupportActivity.this)
                            .load(profilePic)
                            .placeholder(R.drawable.user) // Show placeholder while loading
                            .error(R.drawable.user) // Show error image if loading fails
                            .into(ivuser);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupportActivity.this, "Failed to load admin details", Toast.LENGTH_SHORT).show();
            }
        });



        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList);
        chatRecycler.setAdapter(adapter);

        // Load chat messages from Firebase
        loadChatMessages();
        
        // Test Firebase connection
        testFirebaseConnection();

        back.setOnClickListener(v -> finish());

        // Set up message sending
        setupMessageSending();

        DatabaseReference messagesRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(subAdminId);

        messagesRef.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                ChatMessage msg = messageSnapshot.getValue(ChatMessage.class);
                if (msg != null && !msg.isSeen()) {
                    messageSnapshot.getRef().child("seen").setValue(true);
                }
            }
        });

    }

    private void loadChatMessages() {
        chatRef = FirebaseDatabase.getInstance().getReference("Chats")
                .child(subAdminId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        // Set message ID
                        message.setMessageId(messageSnapshot.getKey());

                        // Format timestamp to readable time
                        String formattedTime = formatTimestamp(message.getTimestamp());
                        message.setTime(formattedTime);

                        // Check if message is from admin or subadmin
                        // If username is "Me" or contains subadmin ID, it's from subadmin (sender)
                        if (message.getUsername() != null &&
                            (message.getUsername().equals("Me") ||
                             message.getUsername().contains(subAdminId))) {
                            message.setSender(true); // Subadmin message (sender)
                        } else {
                            message.setSender(false); // Admin message (receiver)
                            // Set username as admin if not set
                            if (message.getUsername() == null || message.getUsername().isEmpty()) {
                                message.setUsername("Admin");
                            }
                        }

                        chatList.add(message);
                    }
                }

                adapter.notifyDataSetChanged();

                // Scroll to bottom to show latest messages
                if (chatList.size() > 0) {
                    chatRecycler.smoothScrollToPosition(chatList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupportActivity.this, "Error loading chat: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp == 0) return "Now";
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void setupMessageSending() {
        ImageView sendButton = findViewById(R.id.bu_send);
        com.vanniktech.emoji.EmojiEditText messageInput = findViewById(R.id.etm_text);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageInput.setText(""); // Clear input
            }
        });
    }

    private void sendMessage(String messageText) {
        // Create a new message
        ChatMessage newMessage = new ChatMessage(
            messageText,
            formatTimestamp(System.currentTimeMillis()),
            true, // This is from subadmin (sender)
            "Me",
            null, // No admin name for subadmin messages
            null, // No admin profile pic for subadmin messages
            System.currentTimeMillis()
        );

        // Add to local list and notify adapter
        chatList.add(newMessage);
        adapter.notifyDataSetChanged();
        
        // Scroll to bottom
        chatRecycler.smoothScrollToPosition(chatList.size() - 1);

        // Save message to Firebase
        DatabaseReference messageRef = chatRef.push();
        newMessage.setMessageId(messageRef.getKey());
        messageRef.setValue(newMessage)
                .addOnSuccessListener(aVoid -> {
                    // Message saved successfully
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void testFirebaseConnection() {
        FirebaseDatabase.getInstance().getReference("Test").setValue("Test Message")
                .addOnSuccessListener(aVoid -> {
                    // Toast.makeText(this, "Firebase connection successful!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Toast.makeText(this, "Firebase connection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
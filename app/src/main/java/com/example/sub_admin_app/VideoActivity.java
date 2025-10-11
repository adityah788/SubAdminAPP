package com.example.sub_admin_app;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VideoActivity extends AppCompatActivity {

    private YouTubePlayerView ytplayer;
    private DatabaseReference videoRef;
    private SharedPreferences sp;
    private String subAdminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hideSystemUI();

        sp = getSharedPreferences("SubAdminPrefs", MODE_PRIVATE);
        subAdminId = sp.getString("id", "01");

        ytplayer = findViewById(R.id.vvYoutubevideo);
        getLifecycle().addObserver(ytplayer);

        // Load video link from Firebase
        loadVideoFromFirebase();

        // Add test button for debugging
        addTestButton();

        ytplayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // Video will be loaded when Firebase data is retrieved
                Log.d("VideoActivity", "YouTube player is ready");
            }
        });
    }

    private void loadVideoFromFirebase() {
        videoRef = FirebaseDatabase.getInstance().getReference("Admin")
                .child("youtubeLink");

        Log.d("VideoActivity", "Loading video from Firebase...");

        videoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("VideoActivity", "Firebase data changed: " + snapshot.exists());
                if (snapshot.exists()) {
                    String videoLink = snapshot.getValue(String.class);
                    Log.d("VideoActivity", "Video link from Firebase: " + videoLink);
                    if (videoLink != null && !videoLink.isEmpty()) {
                        // Extract video ID from YouTube URL
                        String videoId = extractVideoId(videoLink);
                        Log.d("VideoActivity", "Extracted video ID: " + videoId);
                        if (videoId != null) {
                            loadVideo(videoId);
                        } else {
                            Log.e("VideoActivity", "Invalid YouTube URL: " + videoLink);
                            Toast.makeText(VideoActivity.this, "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
                            // Fallback to default video
                            loadVideo("MnR5YqU-0GI");
                        }
                    } else {
                        Log.d("VideoActivity", "Video link is empty, using default");
                        // Fallback to default video
                        loadVideo("MnR5YqU-0GI");
                    }
                } else {
                    Log.d("VideoActivity", "No video link in Firebase, using default");
                    // Fallback to default video
                    loadVideo("MnR5YqU-0GI");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("VideoActivity", "Error loading video: " + error.getMessage());
                Toast.makeText(VideoActivity.this, "Error loading video: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // Fallback to default video
                loadVideo("MnR5YqU-0GI");
            }
        });
    }

    private String extractVideoId(String url) {
        if (url.contains("youtube.com/watch?v=")) {
            return url.split("v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            return url.split("youtu.be/")[1].split("\\?")[0];
        }
        return null;
    }

    private void loadVideo(String videoId) {
        if (ytplayer != null) {
            ytplayer.getYouTubePlayerWhenReady(youTubePlayer -> {
                youTubePlayer.cueVideo(videoId, 0);
//                Toast.makeText(this, "Loading video: " + videoId, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void addTestButton() {
        // Add a test button to manually trigger video loading
        android.widget.Button testButton = new android.widget.Button(this);
        testButton.setText("Test Video Loading");
        testButton.setOnClickListener(v -> {
            Log.d("VideoActivity", "Test button clicked");
            loadVideoFromFirebase();
        });
        
        // Add to layout if needed
        // For now, just log the click
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // No need to reload video
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }
}
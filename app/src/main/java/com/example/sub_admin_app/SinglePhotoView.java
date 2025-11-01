package com.example.sub_admin_app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.github.chrisbanes.photoview.PhotoView;

public class SinglePhotoView extends AppCompatActivity {

    private PhotoView photoView;
    private ProgressBar progressBar;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_view);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        photoView = findViewById(R.id.pv_image);
        progressBar = findViewById(R.id.pv_imageLoadingProgress);
        back = findViewById(R.id.iv_seepvBack);


        back.setOnClickListener(v-> finish());



        String imageUrl = getIntent().getStringExtra("image_url");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SinglePhotoView.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            return false; // allow Glide to handle the error image
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false; // allow Glide to display the image
                        }
                    })
                    .into(photoView);

        } else {
            Toast.makeText(this, "No image URL received", Toast.LENGTH_SHORT).show();
        }
    }
}

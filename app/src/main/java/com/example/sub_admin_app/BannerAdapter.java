package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private Context context;
    private List<String> images; // Can be Integer (drawable) or String (URL)

    public BannerAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_slide, container, false);
        ImageView imageView = view.findViewById(R.id.bannerImage);

        String imageSource = images.get(position);

        // Check if it's a drawable resource name or URL
        if (imageSource.startsWith("http://") || imageSource.startsWith("https://")) {
            // It's a URL, load with Glide
            Glide.with(context)
                .load(imageSource)
                .placeholder(R.color.shadow) // Fallback image
                .error(R.color.shadow) // Error image
                .into(imageView);
        } else {
            // It's a drawable resource name, load from resources
            try {
                int drawableId = context.getResources().getIdentifier(
                    imageSource, "drawable", context.getPackageName());
                if (drawableId != 0) {
                    imageView.setImageResource(drawableId);
                } else {
                    // Fallback to default image
                    imageView.setImageResource(R.color.shadow);
                }
            } catch (Exception e) {
                // Fallback to default image
                imageView.setImageResource(R.color.shadow);
            }
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}




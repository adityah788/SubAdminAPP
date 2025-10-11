package com.example.sub_admin_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_BANNER = 0;
    private static final int VIEW_TYPE_KEYS = 1;
    private static final int VIEW_TYPE_GRID = 2;

    private Context context;
    private List<String> bannerImages = new ArrayList<>();
    private int availableKeys;
    private int usedKeys, unreadCount = 0;
    private int currentPage = 0;
    private Handler bannerHandler = new Handler();
    private Runnable bannerRunnable;
    private List<String> subAdminIds; // List of IDs for each row
    private DatabaseReference dbRef;
    private List<String> bannerUrls = new ArrayList<>(); // Store banner URLs from Firebase

    public MainAdapter(Context context, List<String> bannerImages, int availableKeys, int usedKeys) {
        this.context = context;
        this.bannerImages = bannerImages;
        this.availableKeys = availableKeys;
        this.usedKeys = usedKeys;
    }

    public void setUnreadCount(int count) {
        this.unreadCount = count;
        notifyDataSetChanged(); // Only needed if you show count per item
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    @Override
    public int getItemCount() {
        return 3; // Banner + Keys Card + Grid
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_BANNER;
        else if (position == 1) return VIEW_TYPE_KEYS;
        else return VIEW_TYPE_GRID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BANNER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
            return new BannerViewHolder(view);
        } else if (viewType == VIEW_TYPE_KEYS) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_keys, parent, false);
            return new KeysViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feature_grid, parent, false);
            return new GridViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_BANNER) {
            BannerViewHolder vh = (BannerViewHolder) holder;

            // Use bannerUrls if available, otherwise use default bannerImages
            List<String> bannersToShow = !bannerUrls.isEmpty() ? bannerUrls : bannerImages;

            if (!bannersToShow.isEmpty()) {
                BannerAdapter adapter = new BannerAdapter(context, bannersToShow);
                vh.viewPager.setAdapter(adapter);

                addDotsIndicator(vh.layoutDots, 0);

                vh.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {}

                    @Override
                    public void onPageSelected(int pos) {
                        addDotsIndicator(vh.layoutDots, pos);
                        currentPage = pos; // track current page for auto-scroll
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {}
                });

                // Auto scroll every 3 seconds
                startAutoScroll(vh.viewPager);
            } else {
                // No banners available, show default
                Log.d("MainAdapter", "No banners available, using default");
            }

        } else if (holder.getItemViewType() == VIEW_TYPE_KEYS) {
            KeysViewHolder kh = (KeysViewHolder) holder;
            if (kh.tvAvailableValue != null)
                kh.tvAvailableValue.setText(String.valueOf(availableKeys));
            if (kh.tvUsedValue != null)
                kh.tvUsedValue.setText(String.valueOf(usedKeys));

        } else if (holder.getItemViewType() == VIEW_TYPE_GRID) {
            GridViewHolder gridHolder = (GridViewHolder) holder;

            // Handle click on tvCopyright
            gridHolder.tvCopyright.setOnClickListener(v -> {
                if (copyrightClickListener != null) {
                    copyrightClickListener.onCopyrightClick();
                }
            });

            gridHolder.tvvideo.setOnClickListener(v ->{
                if (videoClickListener != null) {
                    videoClickListener.onVideoClick();
                }
            });

            gridHolder.rvSupport.setOnClickListener(v -> {
                if(supportClickListener != null){
                    supportClickListener.onSupportClick();
                }
            });

            gridHolder.rvUsers.setOnClickListener(v -> {
                if(userClickListener != null){
                    userClickListener.onUsersClick();
                }
            });

            gridHolder.rvAddAndroid.setOnClickListener(v -> {
                if(androidClickListener != null){
                    androidClickListener.onAndroidClick();
                }
            });

            gridHolder.rvAddIOS.setOnClickListener(v -> {
                if(iosClickListener != null){
                    iosClickListener.onIOSClick();
                }
            });
        }
    }

    public interface OnCopyrightClickListener {
        void onCopyrightClick();
    }

    public interface OnVideoClickListener {
        void onVideoClick();
    }

    public interface OnSupportClickListener {
        void onSupportClick();
    }

    public interface OnUsersClickListener{
        void onUsersClick();
    }

    public interface OnAndroidClickListener{
        void onAndroidClick();
    }

    public interface OnIOSClickListener{
        void onIOSClick();
    }

    private OnCopyrightClickListener copyrightClickListener;
    private OnVideoClickListener videoClickListener;
    private OnSupportClickListener supportClickListener;
    private OnUsersClickListener userClickListener;
    private OnAndroidClickListener androidClickListener;
    private OnIOSClickListener iosClickListener;

    public void setOnCopyrightClickListener(OnCopyrightClickListener listener) {
        this.copyrightClickListener = listener;
    }

    public void setOnVideoClickListener(OnVideoClickListener listener){
        this.videoClickListener = listener;
    }

    public void setOnSupportClickListener(OnSupportClickListener listener){
        this.supportClickListener = listener;
    }

    public void setOnUsersClickListener(OnUsersClickListener listener){
        this.userClickListener = listener;
    }

    public void setAndroidClickListener(OnAndroidClickListener listener){
        this.androidClickListener = listener;
    }

    public void setIosClickListener(OnIOSClickListener listener){
        this.iosClickListener = listener;
    }

    public void updateKeys(int availableKeys, int usedKeys) {
        this.availableKeys = availableKeys;
        this.usedKeys = usedKeys;
        notifyItemChanged(1); // Notify the keys item to update
    }

    public void updateBanners(List<String> newBannerUrls) {
        this.bannerUrls.clear();
        this.bannerUrls.addAll(newBannerUrls);
        Log.d("MainAdapter", "Updated banners: " + bannerUrls.size() + " URLs");
        
        // Reset current page to 0 when banners change
        currentPage = 0;
        
        notifyItemChanged(0); // Notify the banner item to update
    }

    public void resetToDefaultBanners() {
        this.bannerUrls.clear();
        Log.d("MainAdapter", "Reset to default banners");
        currentPage = 0;
        notifyItemChanged(0);
    }

    public void stopAutoScroll() {
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }


    // Dot indicator function
    private void addDotsIndicator(LinearLayout dotsLayout, int currentPage) {
        dotsLayout.removeAllViews();
        
        // Use bannerUrls if available, otherwise use default bannerImages
        List<String> bannersToShow = !bannerUrls.isEmpty() ? bannerUrls : bannerImages;
        
        if (bannersToShow.isEmpty()) {
            return; // No banners to show
        }
        
        TextView[] dots = new TextView[bannersToShow.size()];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }
    }

    private void startAutoScroll(ViewPager viewPager) {
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                // Use bannerUrls if available, otherwise use default bannerImages
                List<String> bannersToShow = !bannerUrls.isEmpty() ? bannerUrls : bannerImages;
                
                if (!bannersToShow.isEmpty()) {
                    currentPage = (currentPage + 1) % bannersToShow.size();
                    viewPager.setCurrentItem(currentPage, true);
                    bannerHandler.postDelayed(this, 3000); // 3 seconds
                }
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 3000);
    }


    // ViewHolders
    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        LinearLayout layoutDots;
        BannerViewHolder(View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.bannerViewPager);
            layoutDots = itemView.findViewById(R.id.layoutDots);
        }
    }

    static class KeysViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvailableValue, tvUsedValue;
        KeysViewHolder(View itemView) {
            super(itemView);
            tvAvailableValue = itemView.findViewById(R.id.tvAvailableValue);
            tvUsedValue = itemView.findViewById(R.id.tvUsedValue);
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        TextView tvCopyright;
        RelativeLayout tvvideo, rvSupport, rvUsers, rvAddAndroid, rvAddIOS;
        @SuppressLint("WrongViewCast")
        GridViewHolder(View itemView) { super(itemView);
            tvCopyright = itemView.findViewById(R.id.tvcopyright);
            tvvideo = itemView.findViewById(R.id.rlYoutube);
            rvSupport = itemView.findViewById(R.id.rlContactSupport);
            rvUsers = itemView.findViewById(R.id.rlUsers);
            rvAddAndroid = itemView.findViewById(R.id.androidPhoneInstallation);
            rvAddIOS = itemView.findViewById(R.id.iphoneInstallation);
        }
    }
}




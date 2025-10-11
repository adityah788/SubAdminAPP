package com.example.sub_admin_app;

public class Banner {
    private String id;
    private String imageUrl;
    private String timestamp;

    public Banner() {
        // Required empty constructor for Firebase
    }

    public Banner(String id, String imageUrl, String timestamp) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

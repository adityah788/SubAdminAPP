package com.example.sub_admin_app;

import java.util.List;

public class Notifications {
    public String message;
    public long timestamp;
    public String adminId;
    public String adminName;
    public String adminProfilePic;
    public List<String> recipients; // comma-separated subadmin IDs
    public String notificationId;

    public Notifications() { }

    public Notifications(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public Notifications(String message, long timestamp, String adminId, String adminName, String adminProfilePic, List<String> recipients) {
        this.message = message;
        this.timestamp = timestamp;
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminProfilePic = adminProfilePic;
        this.recipients = recipients;
    }

    // Getters and setters for Firebase
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
    public String getAdminProfilePic() { return adminProfilePic; }
    public void setAdminProfilePic(String adminProfilePic) { this.adminProfilePic = adminProfilePic; }
    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
}


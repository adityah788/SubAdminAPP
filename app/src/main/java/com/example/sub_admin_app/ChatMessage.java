package com.example.sub_admin_app;

public class ChatMessage {
    private String message;
    private String time;
    private boolean isSender; // true if sent by me
    private boolean seen;
    private String username;
    private int profileRes;
    private String adminName;
    private String adminProfilePic;
    private long timestamp;
    private String messageId;

    public ChatMessage() {
        // Required empty constructor for Firebase
    }

    public ChatMessage(String message, String time, boolean isSender, String username, int profileRes) {
        this.message = message;
        this.time = time;
        this.isSender = isSender;
        this.username = username;
        this.profileRes = profileRes;
    }

    public ChatMessage(String message, String time, boolean isSender, String username, String adminName, String adminProfilePic, long timestamp) {
        this.message = message;
        this.time = time;
        this.isSender = isSender;
        this.username = username;
        this.adminName = adminName;
        this.adminProfilePic = adminProfilePic;
        this.timestamp = timestamp;
    }

    public boolean isSender() { return isSender; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getUsername() { return username; }
    public int getProfileRes() { return profileRes; }
    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
    
    // Firebase getters and setters
    public void setMessage(String message) { this.message = message; }
    public void setTime(String time) { this.time = time; }
    public void setSender(boolean sender) { isSender = sender; }
    public void setUsername(String username) { this.username = username; }
    public void setProfileRes(int profileRes) { this.profileRes = profileRes; }
    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
    public String getAdminProfilePic() { return adminProfilePic; }
    public void setAdminProfilePic(String adminProfilePic) { this.adminProfilePic = adminProfilePic; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
}


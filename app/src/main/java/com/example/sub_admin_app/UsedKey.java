package com.example.sub_admin_app;

public class UsedKey {
    private String keyId;
    private String subAdminId;
    private String customerName;
    private String customerPhone;
    private String deviceType; // "Android" or "iOS"
    private long timestamp;
    private String customerId;

    public UsedKey() {
        // Required empty constructor for Firebase
    }

    public UsedKey(String keyId, String subAdminId, String customerName, String customerPhone, String deviceType, long timestamp, String customerId) {
        this.keyId = keyId;
        this.subAdminId = subAdminId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.deviceType = deviceType;
        this.timestamp = timestamp;
        this.customerId = customerId;
    }

    // Getters and setters
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getSubAdminId() { return subAdminId; }
    public void setSubAdminId(String subAdminId) { this.subAdminId = subAdminId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
}

package com.example.sub_admin_app;

public class Customer {
    private String name;
    private String number;
    private String address;
    private String status; // "Unlock" or "Lock"
    private int profilePicRes; // drawable resource id

    public Customer() {} // Needed for Firebase

    public Customer(String name, String number, String address, String status, int profilePicRes) {
        this.name = name;
        this.number = number;
        this.address = address;
        this.status = status;
        this.profilePicRes = profilePicRes;
    }

    public String getName() { return name; }
    public String getNumber() { return number; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }
    public int getProfilePicRes() { return profilePicRes; }
    public void setStatus(String status) {
        this.status = status;
    }

}


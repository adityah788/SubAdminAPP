package com.example.sub_admin_app;

public class BuyerModel {
    public String buyerId;
    public String name, mobile, email, address, phoneModel, phoneBuild, imei1, imei2;
    public String dop, price, advancePayment, emiAmount,paidEmis, totalEmis;
    public String userPicUrl, docUrl, contractUrl;
    public boolean agreed;

    // ✅ New fields for location
    public double latitude;
    public double longitude;

    public BuyerModel(){}

    // Optional constructor for quick create
    public BuyerModel(String buyerId, String name, String mobile, String email, String address,
                      String phoneModel, String phoneBuild, String imei1, String imei2,
                      String dop, String price, String advancePayment, String emiAmount,String paidEmis, String totalEmis,
                      String userPicUrl, String docUrl, String contractUrl, boolean agreed,
                      double latitude, double longitude) {
        this.buyerId = buyerId;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.phoneModel = phoneModel;
        this.phoneBuild = phoneBuild;
        this.imei1 = imei1;
        this.imei2 = imei2;
        this.dop = dop;
        this.price = price;
        this.advancePayment = advancePayment;
        this.emiAmount = emiAmount;
        this.paidEmis = paidEmis;
        this.totalEmis = totalEmis;
        this.userPicUrl = userPicUrl;
        this.docUrl = docUrl;
        this.contractUrl = contractUrl;
        this.agreed = agreed;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}


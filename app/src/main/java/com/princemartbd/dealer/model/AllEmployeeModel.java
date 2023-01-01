package com.princemartbd.dealer.model;

public class AllEmployeeModel {

    private final String id;
    private final String added_by;
    private final String adder_type;
    private final String name;
    private final String email;
    private final String profile;
    private final String country_code;
    private final String mobile;
    private final String balance;
    private final String fcm_id;
    private final String address;
    private final String area_id;
    private final String city_id;
    private final String pincode_id;
    private final String status;
    private final String type;
    private final String date_create;

    public AllEmployeeModel(String id, String added_by, String adder_type, String name, String email, String profile, String country_code, String mobile, String balance, String fcm_id, String address, String area_id, String city_id, String pincode_id, String status, String type, String date_create) {
        this.id = id;
        this.added_by = added_by;
        this.adder_type = adder_type;
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.country_code = country_code;
        this.mobile = mobile;
        this.balance = balance;
        this.fcm_id = fcm_id;
        this.address = address;
        this.area_id = area_id;
        this.city_id = city_id;
        this.pincode_id = pincode_id;
        this.status = status;
        this.type = type;
        this.date_create = date_create;
    }

    public String getId() {
        return id;
    }

    public String getAdded_by() {
        return added_by;
    }

    public String getAdder_type() {
        return adder_type;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfile() {
        return profile;
    }

    public String getCountry_code() {
        return country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public String getBalance() {
        return balance;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public String getAddress() {
        return address;
    }

    public String getArea_id() {
        return area_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getPincode_id() {
        return pincode_id;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getDate_create() {
        return date_create;
    }
}

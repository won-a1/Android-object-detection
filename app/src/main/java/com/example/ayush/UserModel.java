package com.example.ayush;

public class UserModel {

    private String Name, Email, Mobile, Password, Longitude, Latitude;
    private float SellingPrice,CropQty;

    public UserModel() {}

    public UserModel(String name, String email, String mobile, String password, String longitude, String latitude) {
        Name = name;
        Email = email;
        Mobile = mobile;
        Password = password;
        Longitude = longitude;
        Latitude = latitude;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

}

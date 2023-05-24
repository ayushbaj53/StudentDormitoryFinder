package com.example.studentdormitoryfinder;

public class SearchItem {
    private String location;
    private String price;

    private  String mobile;
    private  String HostelName;

    public SearchItem() {
        // Empty constructor needed for Firebase
    }

    public SearchItem(String location, String price ,String mobile,String HostelName) {
        this.location= location;
        this.price = price;
        this.mobile = mobile;
        this.HostelName = HostelName;

    }
    public String getLocation() {
        return location;
    }
    public String getMobile() {
        return mobile;
    }
    public String getHostelName() {
        return HostelName;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public void setHostelName(String HostelName) {
        this.HostelName = HostelName;
    }


    public void setLocation(String location) {
        this.location = location;
    }

    public String getprice() {
        return price;
    }

    public void setprice(String price) {
        this.price = price;
    }


}

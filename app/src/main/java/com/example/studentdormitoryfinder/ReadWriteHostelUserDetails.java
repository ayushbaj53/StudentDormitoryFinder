package com.example.studentdormitoryfinder;

public class ReadWriteHostelUserDetails {
    public String DoB, gender, mobile,location,price,HostelName;

    //Constructor
    public ReadWriteHostelUserDetails(){};
    public ReadWriteHostelUserDetails(String textDoB, String textGender, String textMobile,String textlocation,String textprice,String textHostelName){
        this.DoB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
        this.location = textlocation;
        this.price = textprice;
        this.HostelName = textHostelName;

    }

}

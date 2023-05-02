package com.example.studentdormitoryfinder;

public class ReadWriteUserDetails {
    public String DoB, gender, mobile;

    //Constructor
    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile){
        this.DoB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }

}

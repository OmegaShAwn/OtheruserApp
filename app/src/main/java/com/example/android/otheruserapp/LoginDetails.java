package com.example.android.otheruserapp;

/**
 * Created by RoshanJoy on 14-03-2017.
 */

public class LoginDetails {

    String Name;
    String password;

    public LoginDetails(){

    }

    public LoginDetails(String Name,String password){
        this.Name=Name;
        this.password=password;
    }

    public String getmUsername(){
        return Name;
    }

    public String getmPassword(){
        return password;
    }

    public void setName(String Name){
        this.Name=Name;
    }

    public void setpassword(String password){
        this.password = password;
    }

}

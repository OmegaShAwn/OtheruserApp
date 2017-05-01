package com.example.android.otheruserapp;

/**
 * Created by RoshanJoy on 17-03-2017.
 */

public class EmergencyDetails {

        public String si;
        public String ti;
        public String username;

        public EmergencyDetails(){}

        public EmergencyDetails(String username){
            this.username=username;
        }

        public void setSi(String si){this.si=si;    }
        public void setTi(String ti){this.ti=ti;}
        public String getSi(){return si;}
        public String getTi(){return ti;}
        public void setUsername(String username){this.username=username;}
        public String getUsername(){return username;}
    }



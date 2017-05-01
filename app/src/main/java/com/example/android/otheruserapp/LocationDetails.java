package com.example.android.otheruserapp;

/**
 * Created by RoshanJoy on 16-03-2017.
 */

public class LocationDetails {

    Double latitude;
    Double longitude;

    public LocationDetails(){}

    public LocationDetails(Double latitude, Double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public void setLatitude(Double latitude){this.latitude=latitude;}
    public void setLongitude(Double longitude){this.longitude=longitude;}
    public Double getLatitude(){return  latitude;}
    public Double getLongitude(){return longitude;}
}

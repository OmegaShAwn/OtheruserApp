package com.example.android.otheruserapp;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.LocationListener;

/**
 * Created by ShAwn on 01-05-2017.
 */

public class locService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,OnMapReadyCallback {


public locService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    public void onDestroy(){
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        locRef.child(username).removeValue();
        super.onDestroy();
    }

    private final String LOG_TAG = "roshantest";
    private LocationRequest mLocationRequest;
    String username;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locRef = database.getReference("Staff");
    private GoogleApiClient mGoogleApiClient;



    public void onCreate () {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent s, int flags, int startId) {
        super.onStartCommand(s, flags, startId);
        username = s.getExtras().getString("username");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        return(Service.START_STICKY_COMPATIBILITY);
    }


    @Override
    public void onMapReady(GoogleMap map) {
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Google api connected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i){
        Log.i(LOG_TAG,"Google api connection has been suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.i(LOG_TAG,"Google api connection has been failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationDetails loc = new LocationDetails(location.getLatitude(), location.getLongitude());
       locRef.child(username).child("locationDetails").setValue(loc);
    }


}

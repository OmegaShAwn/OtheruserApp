package com.example.android.otheruserapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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



    private final String LOG_TAG = "roshantest";
    private LocationRequest mLocationRequest;
    String username;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locRef = database.getReference("Staff");
    private GoogleApiClient mGoogleApiClient;
    int r=0;
    int white=0xfdfdfd;


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

        showNotif();

        return START_STICKY;
    }

    public void showNotif() {

        final PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), Main2Activity.class), 0);

            final Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setTicker("Rajagiri Hospital")
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle("Rajagiri Hospital")
                    .setContentText("Your location is being shared")
                    .setContentIntent(pi)
                    .setColor(white)
                    .setAutoCancel(true)
                    .build();



            startForeground(1337, notification);

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
        if(r==0) {
            LocationDetails loc = new LocationDetails(location.getLatitude(), location.getLongitude());
            locRef.child(username).child("locationDetails").setValue(loc);
        }
    }

    @Override
    public void onDestroy(){
        r=1;
        locRef.child(username).removeValue();
        super.onDestroy();
    }

}

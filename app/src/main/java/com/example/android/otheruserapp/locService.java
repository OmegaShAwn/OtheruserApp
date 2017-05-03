package com.example.android.otheruserapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ShAwn on 01-05-2017.
 */

public class locService extends Service implements  LocationListener {



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
    int r = 0;
    int white = 0xfdfdfd;
    int off;
    int MY_PERMISSION_ACCESS_COARSE_LOCATION=11;

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent s, int flags, int startId) {
        super.onStartCommand(s, flags, startId);
        username = s.getExtras().getString("username");


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            off=1;
        }else{
            off=0;        }

        if(off==0){
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

//            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);


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

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}


    @Override
    public void onLocationChanged(Location location) {
        if (r == 0) {
            LocationDetails loc = new LocationDetails(location.getLatitude(), location.getLongitude());
            locRef.child(username).child("locationDetails").setValue(loc);
        }
    }

    @Override
    public void onDestroy() {
        r = 1;
        locRef.child(username).removeValue();
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent s) {
        r = 1;
        locRef.child(username).removeValue();
        super.stopService(s);
        return super.stopService(s);
    }



}
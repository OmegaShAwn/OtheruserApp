package com.example.android.otheruserapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

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


    String username;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locRef = database.getReference("Staff");
    int r = 0;
    int white = 0xfdfdfd;
    LocationManager locationManager;

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent s, int flags, int startId) {
        super.onStartCommand(s, flags, startId);
        username = s.getExtras().getString("username");


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

        }
        else{
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {}

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            Toast.makeText(getApplicationContext(), "Location is sent", Toast.LENGTH_SHORT).show();
            showNotif();
        }
//        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
        else
            stopSelf();


        return START_STICKY;
    }



    public void showNotif() {

        registerReceiver(stopServiceReceiver, new IntentFilter("myFilter"));
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent("myFilter"), PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setTicker("Rajagiri Hospital")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Rajagiri is receiving your location")
                .setContentText("Click to stop sending location")
                .setContentIntent(pi)
                .setColor(white)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .build();


        startForeground(1337, notification);


    }

    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            Intent qwer = new Intent("finish_activity");
            sendBroadcast(qwer);
//            android.os.Process.killProcess(android.os.Process.myPid());

        }
    };

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}


    @Override
    public void onLocationChanged(Location location) {
        if (r == 0) {
            LocationDetails loc = new LocationDetails(location.getLatitude(), location.getLongitude());
            locRef.child(username).child("locationDetails").setValue(loc);
        }
        else{
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        }
    }


/*    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("service in onTaskRemoved");
        long ct = System.currentTimeMillis(); //get current time
        Intent restartService = new Intent(getApplicationContext(),
                locService.class);
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 0, restartService,
                0);

        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, ct, 1 * 60000, restartServicePI);
    }*/


    @Override
    public void onDestroy() {
        r = 1;
        locationManager.removeUpdates(this);
        locRef.child(username).removeValue();
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent s) {
        r = 1;
        locationManager.removeUpdates(this);
        locRef.child(username).removeValue();
        super.stopService(s);
        return super.stopService(s);
    }



}
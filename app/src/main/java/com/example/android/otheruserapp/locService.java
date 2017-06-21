package com.example.android.otheruserapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by ShAwn on 01-05-2017.
 */

public class locService extends Service implements  LocationListener {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locRef = database.getReference("Staff");
    DatabaseReference logRef = database.getReference("Log/Staff");

    final Calendar c= Calendar.getInstance();
    public locService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }


    String username;
    int r = 0;
    int white = 0xfdfdfd;
    LocationManager locationManager;

    public void onCreate() {
        super.onCreate();

        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        username=settings.getString("lusername","");
        logRef=logRef.child(username);

        logRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                inc();
                if(count>49){
                    final DatabaseReference exceed = database.getReference("Log/Staff/"+username);
                    dec();

                    exceed.addChildEventListener(new ChildEventListener() {

                        int c=0;
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                            c++;
                            if (c != 1) {
                                timeEnded tE = dataSnapshot.getValue(timeEnded.class);
                                exceed.child(Integer.toString(c-1)).setValue(tE);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }

    public int count=0;

    public void inc(){
        count++;
    }

    public void dec() {
        count--;
    }

    @Override
    public int onStartCommand(Intent s, int flags, int startId) {
        super.onStartCommand(s, flags, startId);
        Double lat=0.0,lon=0.0;
        username = s.getExtras().getString("username");
        loc = new LocationDetails(s.getDoubleExtra("lat",lat),s.getDoubleExtra("lon",lon));
        logloc=loc;


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
                .setSmallIcon(R.drawable.ic_stat_untitled)
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


    public int firstime=0;
    @Override
    public void onLocationChanged(Location location) {
        if (r == 0) {
            if(firstime==0){
                logloc = new LocationDetails(location.getLatitude(), location.getLongitude());
                firstime++;
            }
            loc = new LocationDetails(location.getLatitude(), location.getLongitude());
            locRef.child(username).child("locationDetails").setValue(loc);
        }
        else{
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        }
    }

    public LocationDetails loc;
    public LocationDetails logloc;


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
        logRef=logRef.child(Integer.toString(count+1));
        Calendar st = Calendar.getInstance();
        timeEnded tE=new timeEnded();
        tE.setyears(c.get(Calendar.YEAR));
        tE.setmonths(c.get(Calendar.MONTH));
        tE.setdates(c.get(Calendar.DAY_OF_MONTH));
        tE.sethours(c.get(Calendar.HOUR_OF_DAY));
        tE.setminutes(c.get(Calendar.MINUTE));
        tE.setyear(st.get(Calendar.YEAR));
        tE.setmonth(st.get(Calendar.MONTH));
        tE.setdate(st.get(Calendar.DAY_OF_MONTH));
        tE.sethour(st.get(Calendar.HOUR_OF_DAY));
        tE.setminute(st.get(Calendar.MINUTE));
        if(loc!=null) {
            tE.setlatitudee(loc.getLatitude());
            tE.setlongitudee(loc.getLongitude());
        }
        else{
            tE.setlatitudee(0.0);
            tE.setlongitudee(0.0);
        }
        if(logloc!=null) {
            tE.setlatitude(logloc.getLatitude());
            tE.setlongitude(logloc.getLongitude());
        }
        else{
            tE.setlatitude(0.0);
            tE.setlongitude(0.0);
        }
        logRef.setValue(tE);
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
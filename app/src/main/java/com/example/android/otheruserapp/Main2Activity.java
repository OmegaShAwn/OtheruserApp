package com.example.android.otheruserapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;



public class Main2Activity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener ,OnMapReadyCallback{

    MapView mapView;
    GoogleMap googleMap;
    private final String LOG_TAG = "roshantest";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    int firstTime;
    String username;
    Marker marker;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("UserCategories/Otheruser");
    int k;
    public static final int perm=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        final SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        String u=settings.getString("lusername","");


        if(!hasLoggedIn)
        {
            Intent intent=new Intent(Main2Activity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if(!isNetworkAvailable())
            Toast.makeText(getApplicationContext(),"NO INTERNET CONNECTION",Toast.LENGTH_LONG).show();

        username=settings.getString("lusername","");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            if(!dataSnapshot.hasChild(username)){
                Intent intent=new Intent(Main2Activity.this,LoginActivity.class);
                Toast.makeText(Main2Activity.this,"Logged Out",Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
            else{
                Intent s= new Intent(Main2Activity.this, locService.class);
                s.putExtra("username",username);
                startService(s);
                registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        if(!u.equals("")) {
            Toast.makeText(Main2Activity.this, username, Toast.LENGTH_SHORT).show();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        final Button B = (Button) findViewById(R.id.buttonstop);
        firstTime=0;

        int off;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            off=1;
        }else{
            off=0;        }

        if(off==0){
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }



        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, perm );

        }



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

//        txtOutput = (TextView) findViewById(R.id.txtoutput);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(B.getText().equals("STOP")) {
                    Intent s = new Intent(Main2Activity.this, locService.class);
                    stopService(s);
//                    locRef.child(username).removeValue();
                    B.setText("START");
                }
                else{
                    B.setText("STOP");
                    Intent s = new Intent(Main2Activity.this, locService.class);
                    username=settings.getString("lusername","");
                    s.putExtra("username",username);
                    startService(s);
                }
            }});



        if (ActivityCompat.checkSelfPermission(Main2Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ;

        if(googleMap!=null) {
            googleMap.setMyLocationEnabled(true);
            Log.v("main", "googlemap  not null");
            MapsInitializer.initialize(Main2Activity.this);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(55.854049, 13.661331));
            LatLngBounds bounds = builder.build();
            int padding = 0;
            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cameraUpdate);
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case perm: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish_activity")) {
                finish();
                // DO WHATEVER YOU WANT.
            }
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onMapReady(GoogleMap map) {
//DO WHATEVER YOU WANT WITH GOOGLEMAP
        googleMap=map;
        Log.v("main","googlemap set");
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        map.getUiSettings().setZoomControlsEnabled(true);


    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Google api connected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        if (marker != null) {
            marker.remove();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
//        LocationDetails loc = new LocationDetails(location.getLatitude(), location.getLongitude());
//        locRef.child(username).child("locationDetails").setValue(loc);

        if (googleMap != null)
            googleMap.clear();
        MapsInitializer.initialize(Main2Activity.this);

        LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.addMarker(new MarkerOptions()
                .position(coordinate));
        if (firstTime == 0) {
            firstTime++;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate, 17.0f);
            googleMap.moveCamera(cameraUpdate);
        }
    }


    public void onBackPressed()
    {
        if(k==0) {
            Toast.makeText(getApplicationContext(), "Press Back again to log out", Toast.LENGTH_SHORT).show();
            k++;
        }
        else{


            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean("hasLoggedIn",false);
            editor.putString("lusername","");
            editor.commit();
            Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);

            // code here to show dialog
            super.onBackPressed();  // optional depending on your needs
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                k=0;
            }
        }, 2000);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();


    }



}

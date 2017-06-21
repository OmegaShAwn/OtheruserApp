package com.example.android.otheruserapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class location extends FragmentActivity implements OnMapReadyCallback {

    String stime,etime;
    private GoogleMap mMap;
    LatLng coordinate,coordinatee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        stime=extras.getString("stime");
        etime=extras.getString("etime");
        coordinate = new LatLng(extras.getDouble("lat"),extras.getDouble("lon"));
        coordinatee = new LatLng(extras.getDouble("late"),extras.getDouble("lone"));
        if(coordinate.latitude == 0.0 && coordinate.longitude == 0.0)
            Toast.makeText(this,"Location was not received.",Toast.LENGTH_LONG).show();
        if(coordinatee.latitude == 0.0 && coordinatee.longitude == 0.0)
            Toast.makeText(this,"Ending location was not received.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(coordinate).title(stime));
        mMap.addMarker(new MarkerOptions().position(coordinatee).title(etime));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate, 17.0f);
        mMap.moveCamera(cameraUpdate);
    }
}
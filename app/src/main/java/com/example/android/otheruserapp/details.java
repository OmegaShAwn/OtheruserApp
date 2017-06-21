package com.example.android.otheruserapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final TextView start = (TextView)findViewById(R.id.Start);
        final TextView end = (TextView)findViewById(R.id.End);
        final Button B = (Button)findViewById(R.id.Location);

        String username;

        Bundle extras=getIntent().getExtras();
        username=extras.getString("username");
        final int nu=extras.getInt("no");
        final Intent i = new Intent(getApplicationContext(),location.class);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Log/Staff/"+username);

        ref.addChildEventListener(new ChildEventListener() {
            int n=0;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                n++;
                if(n == nu) {
                    if (dataSnapshot != null) {
                    timeEnded tE = dataSnapshot.getValue(timeEnded.class);
                        String t;
                        end.setText("End: "+tE.date+" | "+ tE.month+" | "+ tE.year+" || "+ tE.hour+" : "+ tE.minute);
                        start.setText("Start: "+tE.dates+" | "+ tE.months+" | "+ tE.years+" || "+ tE.hours+" : "+ tE.minutes);
                        i.putExtra("lat",tE.lat);
                        i.putExtra("lon",tE.lon);
                        i.putExtra("late",tE.late);
                        i.putExtra("lone",tE.lone);
                        i.putExtra("stime","Starting Time: "+tE.hours+":"+tE.minutes);
                        i.putExtra("etime","Ending Time: "+tE.hour+":"+tE.minute);
                    }
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

        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });
    }
}

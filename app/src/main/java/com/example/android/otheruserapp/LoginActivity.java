package com.example.android.otheruserapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {




    EditText username ;
    EditText password ;

    boolean usernameFound;
    boolean passwordmatched;

    String currentUserName;
    String currentUserPassword;

    ArrayList<User> users = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("UserCategories/Otheruser");

    public static final String PREFS_NAME = "MyPrefsFile";





    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username =(EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);


        Button signin =(Button)findViewById(R.id.signin);
        Button newaccount =(Button)findViewById(R.id.createAccount);
        newaccount.setVisibility(View.GONE);

        newaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreateAccount = new Intent(LoginActivity.this,createAccountActivity.class);
                startActivity(intentCreateAccount);
            }
        });


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    users.add(user);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameFound=false;
                passwordmatched=false;
                currentUserName = username.getText().toString();
                currentUserPassword = password.getText().toString();
                int i;
                int f;
                f=users.size();
                for( i=0;i<users.size();i++){
                    if ((users.get(i).getUsername()).equals(currentUserName)){
                        usernameFound=true;
                        //Toast.makeText(createAccountActivity.this,"Username already exists",Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                if(usernameFound)
                    if((users.get(i).getPassword().equals(currentUserPassword))){
                        passwordmatched=true;
                    }

                if(usernameFound){
                    if(passwordmatched){
//User has successfully logged in, save this information
// We need an Editor object to make preference changes.
                        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
                        SharedPreferences.Editor editor = settings.edit();

//Set "hasLoggedIn" to true
                        editor.putBoolean("hasLoggedIn", true);
                        editor.putString("lusername",currentUserName);

// Commit the edits!
                        editor.commit();
                        Toast.makeText(LoginActivity.this,"Logged in successfully",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
                        intent.putExtra("username",currentUserName);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Incorrect Password",Toast.LENGTH_LONG).show();
                    }
                }
                else if(f<=0){
                    Toast.makeText(LoginActivity.this,"No Connection",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Invalid Username",Toast.LENGTH_LONG).show();
                }

            }
        });
    }




    @Override
    public void onBackPressed() {

    }

}

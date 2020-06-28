package com.example.myworld.mainactivities;

import android.content.Intent;
import android.os.Bundle;

import com.example.myworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;

public class Splash extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    CollectionReference allLocationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        allLocationRef = firebaseFirestore.collection("Universal Data");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user == null) {

                    Intent myIntent = new Intent(Splash.this, Registration.class);
                    startActivity(myIntent);
                    finish();


                } else {

                    Intent myIntent = new Intent(Splash.this, MapsActivity.class);
                    startActivity(myIntent);
                    finish();

                }


            }
        }, 1000);

    }


}






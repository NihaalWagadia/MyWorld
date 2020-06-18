package com.example.myworld;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    CollectionReference allLocationRef;
    ArrayList<LatLng> latLngs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        allLocationRef = firebaseFirestore.collection("Universal Data");
        Log.d("321", "321");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user == null) {

                            //Log.d("321", String.valueOf(latLngs));
                            Intent myIntent = new Intent(Splash.this, Registration.class);
                            //myIntent.putParcelableArrayListExtra("collection", latLngs);
                            startActivity(myIntent);
                            finish();




                }
                else {
                    Intent myIntent = new Intent(Splash.this, MapsActivity.class);
                    //myIntent.putParcelableArrayListExtra("collection", latLngs);
                    startActivity(myIntent);
                    finish();
//                    readData(new FireStoreCallback() {
//                        @Override
//                        public void onCallback(List<LatLng> latLngList) {
//                            Log.d("321", String.valueOf(latLngs));
//                            Intent myIntent = new Intent(Splash.this, MapsActivity.class);
//                            myIntent.putParcelableArrayListExtra("collection", latLngs);
//                            startActivity(myIntent);
//                            finish();
//                        }
//                    });
//

                }


            }
        }, 1000);

    }

    private void readData(final FireStoreCallback fire) {
        allLocationRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            double lati = documentSnapshot.getDouble("Latitude");
                            double lngi = documentSnapshot.getDouble("Longitude");
                            LatLng latLng = new LatLng(lati, lngi);
                            latLngs.add(latLng);

                        }
                        fire.onCallback(latLngs);
                    }
                });
    }

    private interface FireStoreCallback {
        void onCallback(List<LatLng> latLngList);
    }
}






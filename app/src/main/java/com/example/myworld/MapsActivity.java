package com.example.myworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myworld.model.UserLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView uName;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DocumentReference documentReference;
    CollectionReference allLocationRef;
    String userId;
    String name;
    FirebaseUser user;
    ArrayList<UserLocation>userLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        drawerLayout = findViewById(R.id.drawer_layout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent myIntent = getIntent();
        if (myIntent!= null){
//            latLngs = myIntent.getParcelableArrayListExtra("collection");
        }
//        Log.d("ohhh",String.valueOf(latLngs));
        allLocationRef = firebaseFirestore.collection("Universal Data");

//        readData(new FireStoreCallback() {
//            @Override
//            public void onCallback(List<LatLng> latLngList) {
//                Log.d("321", String.valueOf(latLngs));
//            }
//        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //reference to navigation view. To make it work
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        uName = header.findViewById(R.id.username_header);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        documentReference = firebaseFirestore.collection("People").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                if (documentSnapshot != null) {
                    name = documentSnapshot.getString("Name");
                    uName.setText(name);
                }
            }
        });

//        Log.d("outside", String.valueOf(latLngs));
        for(UserLocation lo : userLocations){
            Log.d("abcd", String.valueOf(lo.getLati()));
        }
    }

    private void getUserLocation() {
        allLocationRef = firebaseFirestore.collection("Universal Data");
        allLocationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots !=null){
                    userLocations = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        UserLocation userLocation = documentSnapshot.toObject(UserLocation.class);
                        userLocations.add(userLocation);
                        Log.d("abcd", String.valueOf(userLocation.getLati()));
                        LatLng latLng = new LatLng(userLocation.getLati(), userLocation.getLongi());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    }
                }

            }
        });

    }

//    private void readData(final FireStoreCallback fire){
//        allLocationRef.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                            double lati = documentSnapshot.getDouble("Latitude");
//                            double lngi = documentSnapshot.getDouble("Longitude");
//                            LatLng latLng = new LatLng(lati, lngi);
//                            latLngs.add(latLng);
//
//                        }
//                        fire.onCallback(latLngs);
//                    }
//                });
//    }

    private interface FireStoreCallback{
        void onCallback(List<LatLng> latLngList);
    }

    private void allUsersLocation() {


//        allLocationRef.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                            double lati = documentSnapshot.getDouble("Latitude");
//                            double lngi = documentSnapshot.getDouble("Longitude");
//                            LatLng latLng = new LatLng(lati, lngi);
//                            latLngs.add(latLng);
//                            Log.d("123", String.valueOf(latLng));
//
//                        }
//                    }
//                });

//                firebaseFirestore1.collection("Universal Data").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            //latLngs = new ArrayList<>();
//                            for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())){
//                                Log.d("MapsActivity", queryDocumentSnapshot.getId() +
//                                        "===" + queryDocumentSnapshot.getData());
//                               // Log.d("material2", String.valueOf(latLngs.size()));
//                                double lati = queryDocumentSnapshot.getDouble("Latitude");
//                                double lngi = queryDocumentSnapshot.getDouble("Longitude");
//                                LatLng latLng = new LatLng(lati, lngi);
//                                latLngs.add(latLng);
////                                DocumentReference documentReference1 = firebaseFirestore.collection("Universal Data").document(queryDocumentSnapshot.getId());
//
//
////                                documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
////                                    @Override
////                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
////                                        //assert documentSnapshot != null;
////                                        if (documentSnapshot != null) {
////                                            double lati = documentSnapshot.getDouble("Latitude");
////                                            double lngi = documentSnapshot.getDouble("Longitude");
////                                            LatLng latLng = new LatLng(lati, lngi);
////                                            latLngs.add(latLng);
////                                            Log.d("Design", String.valueOf(latLngs));
////                                        }
////
////                                    }
////                                });
//
//                               // getLatLng(queryDocumentSnapshot.getId());
//                                Log.d("Design", String.valueOf(latLngs));
//
//                            }
//                        }
//                        else{
//                            Log.d("MapsActivity", "Error:"+ task.getException());
//                        }
//
//                    }
//                });
    }

//    private void getLatLng(String id) {
//        DocumentReference documentReference = firebaseFirestore.collection("Universal Data").document(id);
//
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                //assert documentSnapshot != null;
//                if (documentSnapshot != null) {
//                    double lati = documentSnapshot.getDouble("Latitude");
//                    double lngi = documentSnapshot.getDouble("Longitude");
//                    LatLng latLng = new LatLng(lati, lngi);
//                    latLngs.add(latLng);
//                    Log.d("Design", String.valueOf(latLngs));
//                }
//
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        getUserLocation();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        for(int i=0; i<latLngs.size(); i++){
//            mMap.addMarker(new MarkerOptions().position(latLngs.get(i)).title("Location"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(i)));
//        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_profile) {

            Intent intent = new Intent(this, Profile.class);
//            intent.putParcelableArrayListExtra("collection", latLngs);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
//            Intent intent = new Intent(this, Profile.class);
//            startActivity(intent);

        } else if (id == R.id.nav_upload) {
            Intent intent = new Intent(this, Upload.class);
//            intent.putParcelableArrayListExtra("collection", latLngs);
            startActivity(intent);

        } else if (id == R.id.nav_help) {
//            Intent intent = new Intent(this, Profile.class);
//            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                firebaseAuth.signOut();
                finish();
                Intent myIntent = new Intent(getApplicationContext(), Login.class);
//                myIntent.putParcelableArrayListExtra("collection", latLngs);
                startActivity(myIntent);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}

package com.example.myworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, ConnectionReceiver.ConnectivityReceiverListener {

    private GoogleMap mMap;
    DrawerLayout drawerLayout;
    TextView uName;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DocumentReference documentReference;
    CollectionReference allLocationRef;
    String userId;
    String name;
    ArrayList<UserLocation> userLocations = new ArrayList<>();
    Map<LatLng, String> markerDetailsName = new HashMap<>();
    Map<LatLng, String> markerDetailsImage = new HashMap<>();
    private ClusterManager<MyItem> clusterManager;
    //private List<MyItem>items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)   {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        drawerLayout = findViewById(R.id.drawer_layout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        allLocationRef = firebaseFirestore.collection("Universal Data");
        //check connection manually
        checkInternetConnection();




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

        for (UserLocation lo : userLocations) {
            Log.d("abcd", String.valueOf(lo.getLati()));
        }
    }

    private void checkInternetConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        showSnackBar(isConnected);

        if(!isConnected){
            changeActivity();
        }
    }

    private void changeActivity() {

            Intent intent = new Intent(this, Offline.class);
            startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectionReceiver connectionReceiver = new ConnectionReceiver();
        registerReceiver(connectionReceiver, intentFilter);
        //register connection status listener
        ManualChecker.getInstance().setConnectivityListener(this);
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color;
        if(isConnected){
            message = "You online";
            color = Color.WHITE;
        }
        else{
            message = "check internet";
            color = Color.RED;
        }
        Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    private void getUserLocation() {
        allLocationRef = firebaseFirestore.collection("Universal Data");
        allLocationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    userLocations = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        UserLocation userLocation = documentSnapshot.toObject(UserLocation.class);
                        userLocations.add(userLocation);
                        Log.d("abcd", String.valueOf(userLocation.getLati()));
                        LatLng latLng = new LatLng(userLocation.getLati(), userLocation.getLongi());
                        // customInfoWindowAdapter = new CustomInfoWindowAdapter(getApplicationContext(), userLocation.getUserName(), userLocation.getImageId());

                        markerDetailsName.put(latLng, userLocation.getUserName());
                        markerDetailsImage.put(latLng, userLocation.getImageId());
                        //mMap.addMarker(new MarkerOptions().position(latLng));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                        items.add(new MyItem(latLng));
//                        clusterManager.addItem(items);
                        MyItem offsetItem = new MyItem(latLng);
                        clusterManager.addItem(offsetItem);
                        clusterManager.cluster();

                    }
                }


            }
        });

    }

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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMinZoomPreference(2.0f); // Set a preference for minimum zoom (Zoom out).
        mMap.setMaxZoomPreference(16.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446),1));
        clusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        getUserLocation();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String userNames = markerDetailsName.get(marker.getPosition());
                String imageUrl = markerDetailsImage.get(marker.getPosition());
                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getApplicationContext(), userNames, imageUrl));
                return false;

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_profile) {

            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {


        } else if (id == R.id.nav_upload) {
            Intent intent = new Intent(this, Upload.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {


        } else if (id == R.id.nav_logout) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                firebaseAuth.signOut();
                finish();
                Intent myIntent = new Intent(getApplicationContext(), Login.class);
                startActivity(myIntent);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            changeActivity();
        }
        showSnackBar(isConnected);

    }
}

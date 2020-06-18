package com.example.myworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.LoginFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myworld.model.UserLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Upload extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "Upload";
    private EditText mSearchText;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference, documentReferenceUniversal;
    FirebaseAuth firebaseAuth;
    String userId;
    FirebaseUser currentUser;
    DrawerLayout drawerLayout;
    TextView uName;
    TextView locationResult;
    Button uploadButton, imageButton;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private UserLocation userLocation;
    ImageView imageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mSearchText = findViewById(R.id.input_search);
        locationResult = findViewById(R.id.output_location);
        drawerLayout = findViewById(R.id.drawer_layout_upload);
        uploadButton = findViewById(R.id.upload_button);
        imageButton = findViewById(R.id.button_camera);
        imageView = findViewById(R.id.image_captured);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent myIntent = getIntent();
        latLngArrayList = myIntent.getParcelableArrayListExtra("collection");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_upload);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        uName = header.findViewById(R.id.username_header);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        documentReference = firebaseFirestore.collection("People").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                if (documentSnapshot != null) {
                    uName.setText(documentSnapshot.getString("Name"));
                }
            }
        });

        //Initializing places
        Places.initialize(getApplicationContext(), );
        // Set EditText not focusable
        mSearchText.setFocusable(false);
        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                //create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        , fieldList).build(Upload.this);
                //start activity result
                startActivityForResult(intent, 100);
            }
        });

        //request for camera
        if(ContextCompat.checkSelfPermission(Upload.this,
        Manifest.permission.CAMERA) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Upload.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },100);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1000);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadData();
            }
        });

    }



    private void uploadData() {

        if (mSearchText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Location", Toast.LENGTH_SHORT).show();
            return;
        }
        String latlng = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(this);
        List<Address>list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(latlng,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = list.get(0);
        double lat = address.getLatitude();
        double lng = address.getLongitude();
//        userLocation.setLati(lat);
//        userLocation.setLati(lng);
//        userLocation.setUserId(userId);
        UserLocation userLocation1 = new UserLocation(lat, lng, userId);

        documentReferenceUniversal = firebaseFirestore.collection("Universal Data").document(userId);
//        Map<String, Object> universalData = new HashMap<>();
//        universalData.put("Global Location", mSearchText.getText().toString());
//        universalData.put("Latitude", lat);
//        universalData.put("Longitude", lng);

        documentReferenceUniversal.set(userLocation1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Checker", "checker");
                }
            }
        });

        documentReference = firebaseFirestore.collection("People").document(userId);
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("Location", mSearchText.getText().toString());
        locationData.put("Latitude", lat);
        locationData.put("Longitude", lng);
        documentReference.update(locationData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                intent.putParcelableArrayListExtra("collection", latLngArrayList);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //when success
            //initialize place
            Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
            //set address on EditText
            mSearchText.setText(place.getAddress());
            //set Locality name
            locationResult.setText(String.format("Locality Name : %s", place.getName()));


        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            //when error
            //initialize status
            Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
            //Display Toast
            Toast.makeText(getApplicationContext(), "Alert" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Not authorize", status.getStatusMessage());

        }

        else if(requestCode ==1000){
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            //if(captureImage!=null){
                imageView.setImageBitmap(captureImage);

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_about) {
//            Intent intent = new Intent(this, Profile.class);
//            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, Profile.class);
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
                startActivity(myIntent);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

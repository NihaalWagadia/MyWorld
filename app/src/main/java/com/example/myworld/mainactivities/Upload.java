package com.example.myworld.mainactivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myworld.R;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class Upload extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "Upload";
    static final double COORDINATE_OFFSET = 0.00002; // You can change this value according to your need
    double i = 0.00002;

    EditText mSearchText;
    DocumentReference documentReference, documentReferenceUniversal;
    String userId;
    FirebaseUser currentUser;
    DrawerLayout drawerLayout;
    TextView uName;
    TextView locationResult;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    String nameFetch;
    String currentPhotoPath;
    CollectionReference allLocationRef;
    private int STORAGE_CAMERA = 1010;
    ArrayList<String> userLocations;
    double lat, lng;
    String fileName;
    ImageView imageView;
    Button uploadButton, imageButton;
    Uri imageUrii;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Bitmap compressToFile;
    Uri download_uri;
    String timeStamp;
    ProgressDialog progressDialog;


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
        firebaseFirestore = FirebaseFirestore.getInstance();

        allLocationRef = firebaseFirestore.collection("Universal Data");
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
        storageReference = FirebaseStorage.getInstance().getReference();

        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        documentReference = firebaseFirestore.collection("People").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                if (documentSnapshot != null) {
                    uName.setText(documentSnapshot.getString("Name"));
                    nameFetch = documentSnapshot.getString("Name");
                }
            }
        });

        //Initializing places
        Places.initialize(getApplicationContext(), "");
        // Set EditText not focusable
        mSearchText.setFocusable(false);
        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllLocations();
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                //create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        , fieldList).build(Upload.this);
                //start activity result
                startActivityForResult(intent, 100);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open camera
                requestCameraPermission();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });


    }

    private void getAllLocations() {
        allLocationRef = firebaseFirestore.collection("Universal Data");
        allLocationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    userLocations = new ArrayList<String>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UserLocation userLocation = documentSnapshot.toObject(UserLocation.class);
                        userLocations.add(userLocation.getAddress());
                    }
                }
            }
        });
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("To Upload the image")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(Upload.this, new String[]{Manifest.permission.CAMERA}, STORAGE_CAMERA);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, STORAGE_CAMERA);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadData() {

        if (mSearchText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Location", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("ima", mSearchText.getText().toString());

        if (imageUrii == null) {
            Log.d("ima2", fileName.toString());
            Toast.makeText(getApplicationContext(), "Please Upload image", Toast.LENGTH_SHORT).show();
            return;
        }


        String latlng = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(latlng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = list.get(0);
        lat = address.getLatitude();
        lng = address.getLongitude();

        if (userLocations.contains(address.toString())) {

            double x0 = lat;
            double y0 = lng;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = 500 / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            lat = new_x + x0;
            lng = y + y0;

        }

        documentReferenceUniversal = firebaseFirestore.collection("Universal Data").document(timeStamp);

        UserLocation userLocation1 = new UserLocation(lat, lng, userId, download_uri.toString(), nameFetch, address.toString(), mSearchText.getText().toString());
        documentReferenceUniversal.set(userLocation1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Checker", "checker");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("fail", e.getMessage());
            }
        });

        documentReference = firebaseFirestore.collection("People").document(userId);
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("Location", mSearchText.getText().toString());
        locationData.put("Latitude", lat);
        locationData.put("Longitude", lng);
        locationData.put("imageUrl", download_uri.toString());
        documentReference.update(locationData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
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
            Place place = Autocomplete.getPlaceFromIntent((data));
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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUrii = result.getUri();
                imageView.setImageURI(imageUrii);
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading data");
                progressDialog.show();
                uploadImageUriToFirebase();
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }
    }

    private void uploadImageUriToFirebase() {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File actualImage = new File(imageUrii.getPath());
        try {
            compressToFile = new Compressor(this)
                    .setMaxWidth(320)
                    .setMaxHeight(270)
                    .setQuality(75)
                    .compressToBitmap(actualImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        compressToFile.compress(Bitmap.CompressFormat.JPEG, 100, boas);
        byte[] data = boas.toByteArray();
        Log.d("datata", Arrays.toString(data));
        Log.d("timee", timeStamp);
        UploadTask image_path = storageReference.child("user_images/" + timeStamp + ".jpeg").putBytes(data);

        image_path.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot != null) {
                    Task<Uri> temp_uri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    temp_uri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            download_uri = uri;
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    download_uri = imageUrii;
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Fail", Toast.LENGTH_SHORT).show();
                Log.d("onFail", e.getMessage());
            }
        });
    }


    private void dispatchTakePictureIntent() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(Upload.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
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

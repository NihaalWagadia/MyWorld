package com.example.myworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    TextView registerText;
    FirebaseAuth firebaseAuth;
    Button loginButton;
    FirebaseFirestore firebaseFirestore;
    CollectionReference allLocationRef;

    ArrayList<LatLng> latLngs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        registerText = findViewById(R.id.go_to_register_text);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        allLocationRef = firebaseFirestore.collection("Universal Data");

        Intent myIntent = getIntent();
        if(myIntent!=null) {
//            latLngs = myIntent.getParcelableArrayListExtra("collection");
        }

        Log.d("outside", String.valueOf(latLngs));


    }

    private void readData(final FireStoreCallback fire){
        allLocationRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            double lati = documentSnapshot.getDouble("Latitude");
                            double lngi = documentSnapshot.getDouble("Longitude");
                            LatLng latLng = new LatLng(lati, lngi);
                            latLngs.add(latLng);

                        }
                        fire.onCallback(latLngs);
                    }
                });
    }

    private interface FireStoreCallback{
        void onCallback(List<LatLng> latLngList);
    }

    public void registerPage(View v){
        Intent myIntent = new Intent(Login.this, Registration.class);
        myIntent.putParcelableArrayListExtra("collection", latLngs);
        finish();
    }

    public void login(View v) {

                String emailId = loginEmail.getText().toString().trim();
                String passwordCheck = loginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(emailId)) {
                    loginEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(passwordCheck)) {
                    loginPassword.setError("Password is required");
                    return;
                }


                //authenticate user

                firebaseAuth.signInWithEmailAndPassword(emailId, passwordCheck).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Welcome back", Toast.LENGTH_SHORT).show();

                                    Log.d("321", String.valueOf(latLngs));
                                    Intent myIntent = new Intent(Login.this, MapsActivity.class);
//                                    myIntent.putParcelableArrayListExtra("collection", latLngs);
                                    startActivity(myIntent);
                                    finish();



//
//                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                            intent.putParcelableArrayListExtra(latLngs)
//                            Log.d("okay", String.valueOf(latLngs));
//                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
//                            finish();
                        } else {
                            Toast.makeText(Login.this, "Error !!!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });


            }

    private void allUsersLocation() {
        firebaseFirestore.collection("Universal Data").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //latLngs = new ArrayList<>();
                            for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())){
                                Log.d("Registration", queryDocumentSnapshot.getId() +
                                        "===" + queryDocumentSnapshot.getData());
                                getLatLng(queryDocumentSnapshot.getId());

                                Log.d("material1", String.valueOf(latLngs.size()));

                            }
                        }
                        else{
                            Log.d("MapsActivity", "Error:"+ task.getException());
                        }
                        Log.d("material2", String.valueOf(latLngs.size()));

                    }
                });
    }

    private void getLatLng(String id) {
        DocumentReference documentReference = firebaseFirestore.collection("Universal Data").document(id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //assert documentSnapshot != null;
                if (documentSnapshot != null) {
                    double lati = documentSnapshot.getDouble("Latitude");
                    double lngi = documentSnapshot.getDouble("Longitude");
                    LatLng latLng = new LatLng(lati, lngi);
                    latLngs.add(latLng);
                    Log.d("Design", String.valueOf(latLngs));
                }

            }
        });
    }




    }

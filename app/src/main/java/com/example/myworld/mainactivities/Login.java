package com.example.myworld.mainactivities;

import androidx.annotation.NonNull;
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

import com.example.myworld.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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

    }


    public void registerPage(View v) {
        Intent myIntent = new Intent(Login.this, Registration.class);
//        myIntent.putParcelableArrayListExtra("collection", latLngs);
        startActivity(myIntent);
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
                    startActivity(myIntent);
                    finish();

                } else {
                    Toast.makeText(Login.this, "Error !!!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });


    }


}

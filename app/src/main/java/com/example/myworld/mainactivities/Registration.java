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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registration extends AppCompatActivity {

    public static final String TAG = "Registration";
    EditText userName, password, email;
    FirebaseAuth firebaseAuth;
    Button registerButton;
    TextView loginText;
    FirebaseFirestore firebaseFirestore;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();

        } else {
            setContentView(R.layout.activity_registration);
        }

        userName = findViewById(R.id.register_username);
        password = findViewById(R.id.register_password);
        email = findViewById(R.id.register_email);
        registerButton = findViewById(R.id.registration_button);
        loginText = findViewById(R.id.go_to_login_text);


        //already logged in/existing user

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailId = email.getText().toString().trim();
                String passwordCheck = password.getText().toString().trim();
                final String uName = userName.getText().toString();

                if (TextUtils.isEmpty(emailId)) {
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(passwordCheck)) {
                    password.setError("Password is required");
                    return;
                }

                //registering user

                firebaseAuth.createUserWithEmailAndPassword(emailId, passwordCheck).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this, "User Created", Toast.LENGTH_SHORT).show();
                            //creating/referencing to that field/document
                            userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("People").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", uName);
                            user.put("Email", emailId);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "User Created" + userId);
                                }
                            });
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Registration.this, "Error !!!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

    }


}

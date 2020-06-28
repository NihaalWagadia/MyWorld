package com.example.myworld.backendservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myworld.R;
import com.example.myworld.mainactivities.MapsActivity;

public class Offline extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        button = findViewById(R.id.try_again_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Offline.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

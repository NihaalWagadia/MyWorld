package com.example.myworld;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Upload extends AppCompatActivity {

    public static String TAG = "Upload";
    private EditText mSearchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mSearchText = findViewById(R.id.input_search);
        searchAddress();

    }
    private void searchAddress(){
        Log.d(TAG, "searchAddress: Ready");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                ||keyEvent.getAction() == KeyEvent.ACTION_DOWN
                ||keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    //search function will get started here
                    geoLocate();
                }
                return false;
            }
        });

    }

    private void geoLocate(){
        Log.d(TAG, "geolocate: locationg");
        String search = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(Upload.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(search, 1);
        }
        catch (IOException e){
            Log.d(TAG, "IOException" + e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "Found  location of search"+ address.toString());
        }

    }

}

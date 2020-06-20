package com.example.myworld;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View window;
    private Context context;
    String userName, imageId;

    public CustomInfoWindowAdapter(Context context, String userName, String imageId) {
        this.context = context;
        this.userName = userName;
        this.imageId = imageId;
        window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }


    @Override
    public View getInfoWindow(Marker marker) {
        TextView textView = window.findViewById(R.id.title_name);
        if (userName != null) {
            textView.setText(userName);
        }
        ImageView imageView = window.findViewById(R.id.saved_image);
        if (imageId != null) {
            Picasso.get().load(Uri.parse(imageId)).into(imageView);
        }
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

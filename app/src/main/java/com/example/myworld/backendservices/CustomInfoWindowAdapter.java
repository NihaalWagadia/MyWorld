package com.example.myworld.backendservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.myworld.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class CustomInfoWindowAdapter extends AppGlideModule implements GoogleMap.InfoWindowAdapter {
    private final View window;
    private Context context;
    String userName, imageId;
    boolean not_first_time_showing_info_window;


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
            if (not_first_time_showing_info_window) {
                Picasso.get().load(Uri.parse(imageId)).into(imageView);
            } else {
                not_first_time_showing_info_window = true;
                Picasso.get().load(Uri.parse(imageId)).into(imageView, new InfoWindowRefresher(marker));
            }
        }
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError(Exception e) {

        }
    }

}
